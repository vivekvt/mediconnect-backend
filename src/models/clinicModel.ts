import mongoose, { Document, Schema } from 'mongoose';
import { AddressSchema, IAddress } from './addressModel';

export interface IClinic extends Document {
  userId: number;
  name: string;
  image?: string;
  clinicCode: string;
  address: IAddress['_id'];
  email: string;
  phone: string;
}

const ClinicSchema = new Schema<IClinic>(
  {
    name: {
      type: String,
      default: '',
      // required: true,
    },
    image: {
      type: String,
      default: '',
    },
    clinicCode: {
      type: String,
      required: true,
      unique: true,
    },
    address: {
      type: AddressSchema,
      // required: true,
    },
    email: {
      type: String,
      required: true,
      lowercase: true,
      validate: {
        validator: (v: string) => /^\S+@\S+\.\S+$/.test(v),
        message: 'Invalid email format',
      },
    },
    phone: {
      type: String,
      // required: true,
      validate: {
        validator: (v: string) => /^\+?[\d\s-]{10,}$/.test(v),
        message: 'Invalid phone number format',
      },
    },
  },
  {
    timestamps: true,
  }
);

export const ClinicModel = mongoose.model<IClinic>('Clinic', ClinicSchema);
