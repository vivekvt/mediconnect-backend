import mongoose, { Schema, Document } from 'mongoose';
import { AddressSchema, IAddress } from './addressModel';

export interface IPatient extends Document {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address: IAddress['_id'];
  image?: string;
  governmentIdNumber: string;
  governmentIdFile: string;
  gender: string;
  dateOfBirth: Date;
  clinicCode: string;
}

const PatientSchema = new Schema<IPatient>(
  {
    firstName: {
      type: String,
      // required: true,
      default: '',
    },
    lastName: {
      type: String,
      // required: true,
      default: '',
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
    address: {
      type: AddressSchema,
      // required: true,
    },
    image: {
      type: String,
      default: '',
    },
    governmentIdNumber: {
      type: String,
      default: '',
    },
    governmentIdFile: {
      type: String,
      default: '',
    },
    gender: {
      type: String,
      // required: true,
      default: '',
      enum: ['male', 'female', 'other', ''],
    },
    dateOfBirth: {
      type: Date,
      // required: true,
      default: null,
    },
    clinicCode: {
      type: String,
      // required: true,
      default: '',
    },
  },
  {
    timestamps: true,
  }
);

export const PatientModel = mongoose.model<IPatient>('Patient', PatientSchema);
