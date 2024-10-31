import mongoose, { Document, Schema } from 'mongoose';
import { IClinic } from './clinicModel';

export interface IDoctor extends Document {
  firstName: string;
  lastName: string;
  image: string;
  specialty: string;
  clinicId: IClinic['_id'];
}

const DoctorSchema = new Schema<IDoctor>(
  {
    firstName: {
      type: String,
      required: true,
    },
    lastName: {
      type: String,
      required: true,
    },
    image: {
      type: String,
      default: '',
    },
    specialty: {
      type: String,
      required: true,
    },
    clinicId: {
      type: Schema.Types.ObjectId,
      ref: 'Clinic',
      required: true,
    },
  },
  {
    timestamps: true,
  }
);

export const DoctorModel = mongoose.model<IDoctor>('Doctor', DoctorSchema);
