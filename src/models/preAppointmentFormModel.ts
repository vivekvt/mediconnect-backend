import mongoose, { Document, Schema } from 'mongoose';
import { IAppointment } from './appointmentModel';

export interface IPreAppointmentForm extends Document {
  appointmentId: IAppointment['_id'];
  datetime: Date;
  formDetails: string;
}

const PreAppointmentFormSchema = new Schema<IPreAppointmentForm>(
  {
    appointmentId: {
      type: Schema.Types.ObjectId,
      ref: 'Appointment',
      required: true,
      index: true,
    },
    datetime: {
      type: Date,
      required: true,
      default: Date.now,
    },
    formDetails: {
      type: String,
      required: true,
    },
  },
  {
    timestamps: true,
  }
);

export const PreAppointmentFormModel = mongoose.model<IPreAppointmentForm>(
  'PreAppointmentForm',
  PreAppointmentFormSchema
);
