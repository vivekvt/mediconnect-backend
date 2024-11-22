import { Response } from 'express';
import { PatientModel } from '../models/patientModel';
import { AuthRequest } from '../middlewares/authMiddleware';

export const getPatientProfile = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.user?.userId;

    const patientProfile = await PatientModel.findById(userId);

    if (!patientProfile) {
      return res.status(404).json({ message: 'Patient not found' });
    }

    res.json(patientProfile);
  } catch (error) {
    res.status(500).json({ message: 'Error updating patient profile', error });
  }
};

export const updatePatientProfile = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.user?.userId;

    const {
      firstName,
      lastName,
      email,
      phone,
      address,
      image,
      gender,
      dateOfBirth,
    } = req.body;

    const updatedPatient = await PatientModel.findByIdAndUpdate(
      userId,
      {
        firstName,
        lastName,
        email,
        phone,
        address,
        image,
        gender,
        dateOfBirth,
      },
      { new: true, runValidators: true }
    );

    if (!updatedPatient) {
      return res.status(404).json({ message: 'Patient not found' });
    }

    res.json(updatedPatient);
  } catch (error) {
    res.status(500).json({ message: 'Error updating patient profile', error });
  }
};
