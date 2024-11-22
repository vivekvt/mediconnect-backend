import mongoose, { Schema, Document } from 'mongoose';
import { ISchedule } from './scheduleModel';
import { IPatient } from './patientModel';
import { IDoctor } from './doctorModel';
import { IClinic } from './clinicModel';

export interface IAppointment extends Document {
  status: 'scheduled' | 'confirmed' | 'cancelled' | 'completed';
  scheduleId: ISchedule['_id'];
  patientId: IPatient['_id'];
  doctorId: IDoctor['_id']; // Added for direct doctor reference
  clinicId: IClinic['_id']; // Added for direct clinic reference
  comment?: string;
  paymentId?: string;
  paymentStatus: 'pending' | 'completed' | 'failed' | 'refunded';
  meetingLink?: string;
  reports?: string;
  virtualCheckIn?: any;
  preAppointmentForm?: any;
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
    doctorId: {
      type: Schema.Types.ObjectId,
      ref: 'Doctor',
      required: true,
    },
    clinicId: {
      type: Schema.Types.ObjectId,
      ref: 'Clinic',
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
    virtualCheckIn: {
      type: Object,
      default: {},
    },
    preAppointmentForm: {
      type: Object,
      default: {},
    },
  },
  {
    timestamps: true,
  }
);

// Add indexes for frequent queries
AppointmentSchema.index({ patientId: 1, status: 1 });
AppointmentSchema.index({ doctorId: 1, status: 1 });
AppointmentSchema.index({ clinicId: 1, status: 1 });
AppointmentSchema.index({ scheduleId: 1 }, { unique: true });

export const AppointmentModel = mongoose.model<IAppointment>(
  'Appointment',
  AppointmentSchema
);
