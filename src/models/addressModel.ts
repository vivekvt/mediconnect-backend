import mongoose, { Document, Schema } from 'mongoose';

export interface IAddress extends Document {
  streetAddress: string;
  unitNumber?: string;
  city: string;
  province: string;
  postalCode: string;
  lng: number;
  lat: number;
}

// Schemas
export const AddressSchema = new Schema<IAddress>({
  streetAddress: {
    type: String,
    required: true,
  },
  unitNumber: String,
  city: {
    type: String,
    required: true,
  },
  province: {
    type: String,
    required: true,
  },
  postalCode: {
    type: String,
    required: true,
    uppercase: true,
    trim: true,
  },
  lng: {
    type: Number,
    required: true,
    // index: '2dsphere',
  },
  lat: {
    type: Number,
    required: true,
    // index: '2dsphere',
  },
});
export const Address = mongoose.model<IAddress>('Address', AddressSchema);
