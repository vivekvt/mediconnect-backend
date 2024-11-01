import mongoose, { Document, Schema } from 'mongoose';
import { IAppointment } from './appointmentModel';

interface ICheckIn extends Document {
  appointmentId: IAppointment['_id'];
  datetime: Date;
  checkInDetails: string;
}
const CheckInSchema = new Schema<ICheckIn>(
  {
    appointmentId: {
      type: Schema.Types.ObjectId,
      ref: 'Appointment',
      required: true,
    },
    datetime: {
      type: Date,
      required: true,
      default: Date.now,
    },
    checkInDetails: {
      type: String,
      required: true,
    },
  },
  {
    timestamps: true,
  }
);
export const CheckIn = mongoose.model<ICheckIn>('CheckIn', CheckInSchema);
