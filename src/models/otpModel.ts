import mongoose, { Schema, Document } from 'mongoose';

export interface IOTP extends Document {
  email: string;
  otp: string;
  role: 'clinic' | 'patient';
  expiresAt: Date;
}

const OTPSchema = new Schema<IOTP>({
  email: {
    type: String,
    required: true,
    lowercase: true,
  },
  otp: {
    type: String,
    required: true,
  },
  role: {
    type: String,
    enum: ['clinic', 'patient'],
    required: true,
  },
  expiresAt: {
    type: Date,
    required: true,
  },
});

// Index that automatically deletes expired OTPs
OTPSchema.index({ expiresAt: 1 }, { expireAfterSeconds: 0 });

export const OTPModel = mongoose.model<IOTP>('OTP', OTPSchema);
