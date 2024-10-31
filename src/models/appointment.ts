import mongoose, { Schema, Document } from 'mongoose';
import { ISchedule } from './scheduleModel';
import { IPatient } from './patientModel';

export interface IAppointment extends Document {
  status: string;
  scheduleId: ISchedule['_id'];
  patientId: IPatient['_id'];
  comment?: string;
  paymentId?: string;
  paymentStatus: string;
  meetingLink?: string;
  reports?: string;
}

const AppointmentSchema = new Schema<IAppointment>(
  {
    status: {
      type: String,
      required: true,
      enum: ['scheduled', 'confirmed', 'cancelled', 'completed'],
      default: 'scheduled',
    },
    scheduleId: {
      type: Schema.Types.ObjectId,
      ref: 'Schedule',
      required: true,
    },
    patientId: {
      type: Schema.Types.ObjectId,
      ref: 'Patient',
      required: true,
    },
    comment: String,
    paymentId: String,
    paymentStatus: {
      type: String,
      required: true,
      enum: ['pending', 'completed', 'failed', 'refunded'],
      default: 'pending',
    },
    meetingLink: String,
    reports: String,
  },
  {
    timestamps: true,
  }
);

export const AppointmentModel = mongoose.model<IAppointment>(
  'Appointment',
  AppointmentSchema
);
