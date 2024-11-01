import { Response } from 'express';
import { AuthRequest } from '../middlewares/authMiddleware';
import { AppointmentModel } from '../models/appointmentModel';
import { ScheduleModel } from '../models/scheduleModel';
import mongoose from 'mongoose';

export const createAppointment = async (req: AuthRequest, res: Response) => {
  try {
    const { scheduleId, patientId, comment } = req.body;

    // Get the schedule to verify and get doctor/clinic info
    const schedule: any = await ScheduleModel.findById(scheduleId).populate(
      'doctorId'
    );
    if (!schedule) {
      return res.status(404).json({ message: 'Schedule not found' });
    }

    // Check if schedule is already booked
    const existingAppointment = await AppointmentModel.findOne({ scheduleId });
    if (existingAppointment) {
      return res.status(400).json({ message: 'Schedule already booked' });
    }
    const appointment = new AppointmentModel({
      scheduleId,
      patientId,
      doctorId: schedule.doctorId,
      clinicId: schedule.doctorId.clinicId,
      comment,
      status: 'scheduled',
      paymentStatus: 'pending',
    });

    await appointment.save();
    res.status(201).json(appointment);
  } catch (error) {
    res.status(500).json({ message: 'Error creating appointment', error });
  }
};

// Get appointments by patient ID
export const getPatientAppointments = async (
  req: AuthRequest,
  res: Response
) => {
  try {
    const userId = req.user?.userId;
    const { status } = req.query;

    const query: any = { patientId: userId };
    if (status) {
      query.status = status;
    }

    const appointments = await AppointmentModel.find(query)
      .populate('scheduleId')
      .populate('doctorId')
      .populate('clinicId')
      .sort({ createdAt: -1 });

    res.json(appointments);
  } catch (error) {
    res.status(500).json({ message: 'Error fetching appointments', error });
  }
};

// Get appointments by doctor ID
export const getDoctorAppointments = async (
  req: AuthRequest,
  res: Response
) => {
  try {
    const { doctorId } = req.params;
    const { status, startDate, endDate } = req.query;

    const query: any = { doctorId };
    if (status) {
      query.status = status;
    }

    if (startDate && endDate) {
      query.createdAt = {
        $gte: new Date(startDate as string),
        $lte: new Date(endDate as string),
      };
    }

    const appointments = await AppointmentModel.find(query)
      .populate('scheduleId')
      .populate('patientId')
      .sort({ createdAt: -1 });

    res.json(appointments);
  } catch (error) {
    res.status(500).json({ message: 'Error fetching appointments', error });
  }
};

// Get appointments by clinic ID
export const getClinicAppointments = async (
  req: AuthRequest,
  res: Response
) => {
  try {
    const clinicId = req.user?.userId;
    const { status, startDate, endDate } = req.query;

    const query: any = { clinicId };
    if (status) {
      query.status = status;
    }

    if (startDate && endDate) {
      query.createdAt = {
        $gte: new Date(startDate as string),
        $lte: new Date(endDate as string),
      };
    }

    const appointments = await AppointmentModel.find(query)
      .populate('scheduleId')
      .populate('patientId')
      .populate('doctorId')
      .sort({ createdAt: -1 });

    res.json(appointments);
  } catch (error) {
    res.status(500).json({ message: 'Error fetching appointments', error });
  }
};

// Get appointment by ID
export const getAppointmentById = async (req: AuthRequest, res: Response) => {
  try {
    const appointment = await AppointmentModel.findById(req.params.id)
      .populate('scheduleId')
      .populate('patientId')
      .populate('doctorId')
      .populate('clinicId');

    if (!appointment) {
      return res.status(404).json({ message: 'Appointment not found' });
    }

    res.json(appointment);
  } catch (error) {
    res.status(500).json({ message: 'Error fetching appointment', error });
  }
};

// Update appointment
export const updateAppointment = async (req: AuthRequest, res: Response) => {
  try {
    const { status, comment, paymentStatus, meetingLink, reports } = req.body;

    const appointment = await AppointmentModel.findById(req.params.id);

    if (!appointment) {
      return res.status(404).json({ message: 'Appointment not found' });
    }

    // Validate status transition
    if (status && !isValidStatusTransition(appointment.status, status)) {
      return res.status(400).json({
        message: 'Invalid status transition',
        currentStatus: appointment.status,
        requestedStatus: status,
      });
    }

    const updatedAppointment = await AppointmentModel.findByIdAndUpdate(
      req.params.id,
      {
        status,
        comment,
        paymentStatus,
        meetingLink,
        reports,
        ...(status === 'completed' ? { completedAt: new Date() } : {}),
      },
      { new: true, runValidators: true }
    ).populate(['scheduleId', 'patientId', 'doctorId', 'clinicId']);

    res.json(updatedAppointment);
  } catch (error) {
    res.status(500).json({ message: 'Error updating appointment', error });
  }
};

// Cancel appointment
export const cancelAppointment = async (req: AuthRequest, res: Response) => {
  try {
    // const { reason } = req.body;
    const appointment = await AppointmentModel.findById(req.params.id);

    if (!appointment) {
      return res.status(404).json({ message: 'Appointment not found' });
    }

    if (appointment.status === 'completed') {
      return res
        .status(400)
        .json({ message: 'Cannot cancel completed appointment' });
    }

    const updatedAppointment = await AppointmentModel.findByIdAndUpdate(
      req.params.id,
      {
        status: 'cancelled',
      },
      { new: true }
    ).populate(['scheduleId', 'patientId', 'doctorId', 'clinicId']);

    res.json(updatedAppointment);
  } catch (error) {
    res.status(500).json({ message: 'Error cancelling appointment', error });
  }
};

// Get appointment statistics for clinic
export const getClinicAppointmentStats = async (
  req: AuthRequest,
  res: Response
) => {
  try {
    const { clinicId } = req.params;

    const stats = await AppointmentModel.aggregate([
      { $match: { clinicId: new mongoose.Types.ObjectId(clinicId) } },
      {
        $group: {
          _id: {
            status: '$status',
            month: { $month: '$createdAt' },
            year: { $year: '$createdAt' },
          },
          count: { $sum: 1 },
        },
      },
      {
        $group: {
          _id: { month: '$_id.month', year: '$_id.year' },
          statuses: {
            $push: {
              status: '$_id.status',
              count: '$count',
            },
          },
          totalAppointments: { $sum: '$count' },
        },
      },
      { $sort: { '_id.year': -1, '_id.month': -1 } },
    ]);

    res.json(stats);
  } catch (error) {
    res
      .status(500)
      .json({ message: 'Error fetching appointment statistics', error });
  }
};

// Helper function to validate status transitions
const isValidStatusTransition = (
  currentStatus: string,
  newStatus: string
): boolean => {
  const validTransitions: { [key: string]: string[] } = {
    scheduled: ['confirmed', 'cancelled'],
    confirmed: ['completed', 'cancelled'],
    cancelled: [], // No transitions allowed from cancelled
    completed: [], // No transitions allowed from completed
  };

  return validTransitions[currentStatus]?.includes(newStatus) || false;
};
