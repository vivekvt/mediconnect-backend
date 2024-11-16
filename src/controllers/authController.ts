import { Request, Response } from 'express';
import { OTPModel } from '../models/otpModel';
import { ClinicModel } from '../models/clinicModel';
import { PatientModel } from '../models/patientModel';
import { resend } from '../config/resend';
import { generateToken } from '../utils/jwt';
import { AuthRequest } from '../middlewares/authMiddleware';

export const getOtp = async (req: Request, res: Response) => {
  try {
    const { email, role } = req.body;

    if (!email || !role) {
      return res.status(400).json({ message: 'Email and role are required' });
    }

    if (!['clinic', 'patient'].includes(role)) {
      return res.status(400).json({ message: 'Invalid role' });
    }

    // Check and create user based on role if they don't exist
    if (role === 'clinic') {
      const patient = await PatientModel.findOne({ email });
      if (patient?._id) {
        return res.status(400).json({
          message:
            'Patient account with this email already exists, use new email to signup as clinic',
        });
      }
      let clinic = await ClinicModel.findOne({ email });
      if (!clinic) {
        // Generate random 4-digit clinic code
        let clinicCode = Math.floor(1000 + Math.random() * 9000).toString();

        // Ensure clinic code is unique s
        while (await ClinicModel.findOne({ clinicCode })) {
          clinicCode = Math.floor(1000 + Math.random() * 9000).toString();
        }

        clinic = await ClinicModel.create({
          email,
          clinicCode,
          // Add any other required default fields for new clinic
        });
      }
    } else {
      const clinic = await ClinicModel.findOne({ email });
      if (clinic?._id) {
        return res.status(400).json({
          message:
            'Clinic account with this email already exists, use new email to signup as patient',
        });
      }
      let patient = await PatientModel.findOne({ email });
      if (!patient) {
        patient = await PatientModel.create({
          email,
          // Add any required default fields for new patient
        });
      }
    }

    // Generate 6-digit OTP
    const otp = Math.floor(100000 + Math.random() * 900000).toString();

    // Delete any existing OTP for this email
    await OTPModel.deleteMany({ email });

    // Save new OTP to database
    await OTPModel.create({
      email,
      otp,
      role,
      expiresAt: new Date(Date.now() + 5 * 60 * 1000), // 5 minutes expiry
    });

    // Send OTP via email using Resend
    await resend.emails.send({
      from: 'contact@mediconnect-dev.vivekthakur.dev',
      to: email,
      subject: 'Your OTP Code',
      html: `Your OTP code is: <strong>${otp}</strong>. It will expire in 5 minutes.`,
    });

    res.json({ message: 'OTP sent successfully' });
  } catch (error) {
    console.error('Error in getOtp:', error);
    res.status(500).json({ message: 'Error sending OTP' });
  }
};

export const verifyOtp = async (req: Request, res: Response) => {
  try {
    const { email, otp } = req.body;

    const otpDoc = await OTPModel.findOne({
      email,
      otp,
      expiresAt: { $gt: new Date() },
    });

    if (!otpDoc) {
      return res.status(400).json({ message: 'Invalid or expired OTP' });
    }

    // Get user profile based on role
    let profile;
    if (otpDoc.role === 'clinic') {
      profile = await ClinicModel.findOne({ email }).populate('address');
      if (!profile) {
        return res.status(404).json({ message: 'Clinic not found' });
      }
    } else {
      profile = await PatientModel.findOne({ email });
      if (!profile) {
        return res.status(404).json({ message: 'Patient not found' });
      }
    }

    // Generate JWT token using profile._id
    const token = generateToken(profile._id, otpDoc.role);

    // Delete used OTP
    await OTPModel.deleteOne({ _id: otpDoc._id });

    res.json({
      token,
      profile,
      role: otpDoc.role,
    });
  } catch (error) {
    console.error('Error in verifyOtp:', error);
    res.status(500).json({ message: 'Error verifying OTP' });
  }
};

export const getProfile = async (req: AuthRequest, res: Response) => {
  try {
    const { userId, role } = req.user!;

    let profile;
    if (role === 'clinic') {
      profile = await ClinicModel.findById(userId).populate('address');
    } else {
      profile = await PatientModel.findById(userId);
    }

    if (!profile) {
      return res.status(404).json({ message: 'Profile not found' });
    }

    res.json({ profile, role });
  } catch (error) {
    console.error('Error in getProfile:', error);
    res.status(500).json({ message: 'Error fetching profile' });
  }
};
