import { Response } from 'express';
import { AuthRequest } from '../middlewares/authMiddleware';
import { ScheduleModel } from '../models/scheduleModel';
import { DoctorModel } from '../models/doctorModel';
import { AppointmentModel } from '../models/appointmentModel';

export const createSchedule = async (req: AuthRequest, res: Response) => {
  try {
    const { doctorId, startDateTime, endDateTime, isOnline } = req.body;

    // Verify if the doctor exists and belongs to the clinic
    const doctor = await DoctorModel.findOne({
      _id: doctorId,
      clinicId: req.user?.userId,
    });

    if (!doctor) {
      return res
        .status(404)
        .json({ message: 'Doctor not found or unauthorized' });
    }

    // Check for schedule conflicts
    const conflictingSchedule = await ScheduleModel.findOne({
      doctorId,
      $or: [
        {
          startDateTime: { $lt: endDateTime },
          endDateTime: { $gt: startDateTime },
        },
      ],
    });

    if (conflictingSchedule) {
      return res.status(400).json({ message: 'Schedule conflict detected' });
    }

    const schedule = new ScheduleModel({
      doctorId,
      startDateTime,
      endDateTime,
      isOnline,
    });

    await schedule.save();
    res.status(201).json(schedule);
  } catch (error) {
    res.status(500).json({ message: 'Error creating schedule', error });
  }
};

export const getSchedulesByDoctor = async (req: AuthRequest, res: Response) => {
  try {
    const { doctorId } = req.params;
    const { startDate, endDate } = req.query;

    if (!startDate || !endDate) {
      throw new Error('startDate, endDate is required');
    }

    // Verify if the doctor belongs to the clinic
    const doctor = await DoctorModel.findOne({
      _id: doctorId,
    });

    if (!doctor) {
      return res
        .status(404)
        .json({ message: 'Doctor not found or unauthorized' });
    }

    const query: any = { doctorId };

    // Add date range filter if provided
    if (startDate && endDate) {
      query.startDateTime = {
        $gte: new Date(startDate as string),
        $lte: new Date(endDate as string),
      };
    }

    // Get all schedules for the doctor
    const schedules: any = await ScheduleModel.find(query).sort({
      startDateTime: 1,
    });

    // Get all appointments for these schedules
    const appointments: any = await AppointmentModel.find({
      scheduleId: { $in: schedules.map((schedule: any) => schedule._id) },
    });

    // Create a map of schedule IDs to appointments
    const scheduleAppointments = appointments.reduce(
      (acc: any, appointment: any) => {
        acc[appointment.scheduleId.toString()] = appointment;
        return acc;
      },
      {} as { [key: string]: any }
    );

    // Add availability status to each schedule
    const schedulesWithAvailability = schedules.map((schedule: any) => {
      const scheduleObj: any = schedule.toObject();
      const appointment = scheduleAppointments[schedule._id.toString()];

      scheduleObj.isAvailable =
        !appointment || appointment.status === 'cancelled';

      // Optionally, you can also include the appointment status if needed
      if (appointment) {
        scheduleObj.appointmentStatus = appointment.status;
      }

      return scheduleObj;
    });

    res.json(schedulesWithAvailability);
  } catch (error: any) {
    res
      .status(500)
      .json({ message: `Error fetching schedules: ${error?.message}` });
  }
};

export const getScheduleById = async (req: AuthRequest, res: Response) => {
  try {
    const schedule = await ScheduleModel.findById(req.params.id).populate(
      'doctorId'
    );

    if (!schedule) {
      return res.status(404).json({ message: 'Schedule not found' });
    }

    // Verify if the schedule's doctor belongs to the clinic
    const doctor = await DoctorModel.findOne({
      _id: schedule.doctorId,
      clinicId: req.user?.userId,
    });

    if (!doctor) {
      return res
        .status(403)
        .json({ message: 'Unauthorized access to schedule' });
    }

    res.json(schedule);
  } catch (error) {
    res.status(500).json({ message: 'Error fetching schedule', error });
  }
};

export const updateSchedule = async (req: AuthRequest, res: Response) => {
  try {
    const { startDateTime, endDateTime, isOnline } = req.body;
    const scheduleId = req.params.id;

    const existingSchedule = await ScheduleModel.findById(scheduleId);
    if (!existingSchedule) {
      return res.status(404).json({ message: 'Schedule not found' });
    }

    // Verify if the schedule's doctor belongs to the clinic
    const doctor = await DoctorModel.findOne({
      _id: existingSchedule.doctorId,
      clinicId: req.user?.userId,
    });

    if (!doctor) {
      return res
        .status(403)
        .json({ message: 'Unauthorized access to schedule' });
    }

    // Check for schedule conflicts (excluding current schedule)
    const conflictingSchedule = await ScheduleModel.findOne({
      _id: { $ne: scheduleId },
      doctorId: existingSchedule.doctorId,
      $or: [
        {
          startDateTime: { $lt: endDateTime },
          endDateTime: { $gt: startDateTime },
        },
      ],
    });

    if (conflictingSchedule) {
      return res.status(400).json({ message: 'Schedule conflict detected' });
    }

    const updatedSchedule = await ScheduleModel.findByIdAndUpdate(
      scheduleId,
      {
        startDateTime,
        endDateTime,
        isOnline,
      },
      { new: true }
    );

    res.json(updatedSchedule);
  } catch (error) {
    res.status(500).json({ message: 'Error updating schedule', error });
  }
};

export const deleteSchedule = async (req: AuthRequest, res: Response) => {
  try {
    const schedule = await ScheduleModel.findById(req.params.id);

    if (!schedule) {
      return res.status(404).json({ message: 'Schedule not found' });
    }

    // Verify if the schedule's doctor belongs to the clinic
    const doctor = await DoctorModel.findOne({
      _id: schedule.doctorId,
      clinicId: req.user?.userId,
    });

    if (!doctor) {
      return res
        .status(403)
        .json({ message: 'Unauthorized access to schedule' });
    }

    await ScheduleModel.findByIdAndDelete(req.params.id);
    res.json({ message: 'Schedule deleted successfully' });
  } catch (error) {
    res.status(500).json({ message: 'Error deleting schedule', error });
  }
};
