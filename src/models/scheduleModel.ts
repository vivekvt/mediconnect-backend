import mongoose, { Schema, Document } from 'mongoose';
import { IDoctor } from './doctorModel';

export interface ISchedule extends Document {
  doctorId: IDoctor['_id'];
  startDateTime: Date;
  endDateTime: Date;
  isOnline: boolean;
}

const ScheduleSchema = new Schema<ISchedule>(
  {
    doctorId: {
      type: Schema.Types.ObjectId,
      ref: 'Doctor',
      required: true,
    },
    startDateTime: {
      type: Date,
      required: true,
    },
    endDateTime: {
      type: Date,
      required: true,
    },
    isOnline: {
      type: Boolean,
      default: false,
    },
  },
  {
    timestamps: true,
  }
);

export const ScheduleModel = mongoose.model<ISchedule>(
  'Schedule',
  ScheduleSchema
);
