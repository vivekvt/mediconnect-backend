import { Response } from 'express';
import { ClinicModel } from '../models/clinicModel';
import { AuthRequest } from '../middlewares/authMiddleware';
import { DoctorModel } from '../models/doctorModel';

export const getClinicProfile = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.user?.userId;

    const clinicProfile = await ClinicModel.findById(userId);

    if (!clinicProfile) {
      return res.status(404).json({ message: 'Clinic not found' });
    }

    res.json(clinicProfile);
  } catch (error) {
    res.status(500).json({ message: 'Error updating clinic profile', error });
  }
};

export const getClinicByClinicCode = async (
  req: AuthRequest,
  res: Response
) => {
  try {
    const clinicCode = req.params?.clinicCode || '';

    const clinicProfile = await ClinicModel.findOne({ clinicCode });

    if (!clinicProfile) {
      return res.status(404).json({ message: 'Clinic not found' });
    }

    res.json(clinicProfile);
  } catch (error) {
    res.status(500).json({ message: 'Error updating clinic profile', error });
  }
};

export const updateClinicProfile = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.user?.userId;
    const { name, email, phone, address, image } = req.body;
    const updatedClinic = await ClinicModel.findByIdAndUpdate(
      userId,
      {
        name,
        email,
        phone,
        address,
        image,
      },
      { new: true, runValidators: true }
    );

    if (!updatedClinic) {
      return res.status(404).json({ message: 'Clinic not found' });
    }

    res.json(updatedClinic);
  } catch (error) {
    console.log(error);
    res.status(500).json({ message: 'Error updating clinic profile', error });
  }
};

export const createDoctor = async (req: AuthRequest, res: Response) => {
  try {
    const { firstName, lastName, specialty } = req.body;
    const clinic = await ClinicModel.findById(req.user?.userId);

    if (!clinic) {
      return res.status(404).json({ message: 'Clinic not found' });
    }

    const doctor = new DoctorModel({
      firstName,
      lastName,
      specialty,
      clinicId: clinic._id,
    });

    await doctor.save();
    res.status(201).json(doctor);
  } catch (error) {
    res.status(500).json({ message: 'Error creating doctor', error });
  }
};

export const getAllDoctors = async (req: AuthRequest, res: Response) => {
  try {
    const clinic = await ClinicModel.findById(req.user?.userId);

    if (!clinic) {
      return res.status(404).json({ message: 'Clinic not found' });
    }

    const doctors = await DoctorModel.find({ clinicId: clinic._id });
    res.json(doctors);
  } catch (error) {
    res.status(500).json({ message: 'Error fetching doctors', error });
  }
};

export const getAllDoctorByClinicId = async (
  req: AuthRequest,
  res: Response
) => {
  try {
    const clinic = await ClinicModel.findById(req.params?.clinicId);

    if (!clinic) {
      return res.status(404).json({ message: 'Clinic not found' });
    }

    const doctors = await DoctorModel.find({ clinicId: clinic._id });
    res.json(doctors);
  } catch (error) {
    res.status(500).json({ message: 'Error fetching doctors', error });
  }
};

export const getDoctorById = async (req: AuthRequest, res: Response) => {
  try {
    const doctor = await DoctorModel.findById(req.params.id);
    if (!doctor) {
      return res.status(404).json({ message: 'Doctor not found' });
    }
    res.json(doctor);
  } catch (error) {
    res.status(500).json({ message: 'Error fetching doctor', error });
  }
};

export const updateDoctor = async (req: AuthRequest, res: Response) => {
  try {
    const { firstName, lastName, specialty } = req.body;
    const doctor = await DoctorModel.findByIdAndUpdate(
      req.params.id,
      {
        firstName,
        lastName,
        specialty,
      },
      { new: true }
    );

    if (!doctor) {
      return res.status(404).json({ message: 'Doctor not found' });
    }

    res.json(doctor);
  } catch (error) {
    res.status(500).json({ message: 'Error updating doctor', error });
  }
};

export const deleteDoctor = async (req: AuthRequest, res: Response) => {
  try {
    const doctor = await DoctorModel.findByIdAndDelete(req.params.id);
    if (!doctor) {
      return res.status(404).json({ message: 'Doctor not found' });
    }
    res.json({ message: 'Doctor deleted successfully' });
  } catch (error) {
    res.status(500).json({ message: 'Error deleting doctor', error });
  }
};
