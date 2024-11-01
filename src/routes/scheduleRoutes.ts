import express from 'express';
import { body, query } from 'express-validator';
import { validateRequest } from '../middlewares/validation';
import { authenticateUser, isClinic } from '../middlewares/authMiddleware';
import {
  createSchedule,
  getSchedulesByDoctor,
  getScheduleById,
  updateSchedule,
  deleteSchedule,
} from '../controllers/scheduleController';

const router = express.Router();

// Validation middleware
const scheduleValidation = [
  body('doctorId').isMongoId().withMessage('Valid doctor ID is required'),
  body('startDateTime')
    .isISO8601()
    .withMessage('Valid start date-time is required'),
  body('endDateTime')
    .isISO8601()
    .withMessage('Valid end date-time is required')
    .custom((endDateTime, { req }) => {
      if (new Date(endDateTime) <= new Date(req.body.startDateTime)) {
        throw new Error('End time must be after start time');
      }
      return true;
    }),
  body('isOnline').optional().isBoolean(),
];

const dateRangeValidation = [
  query('startDate').optional().isISO8601(),
  query('endDate')
    .optional()
    .isISO8601()
    .custom((endDate, { req }: any) => {
      if (
        req.query.startDate &&
        new Date(endDate) <= new Date(req.query.startDate as string)
      ) {
        throw new Error('End date must be after start date');
      }
      return true;
    }),
];

// Get schedules by doctor ID with optional date range
router.get(
  '/doctor/:doctorId',
  dateRangeValidation,
  validateRequest,
  getSchedulesByDoctor
);

// Apply clinic authentication to all routes
router.use(authenticateUser, isClinic);

// Create new schedule
router.post('/', scheduleValidation, validateRequest, createSchedule);

// Get specific schedule by ID
router.get('/:id', getScheduleById);

// Update schedule
router.patch(
  '/:id',
  [
    body('startDateTime').optional().isISO8601(),
    body('endDateTime')
      .optional()
      .isISO8601()
      .custom((endDateTime, { req }) => {
        if (
          endDateTime &&
          req.body.startDateTime &&
          new Date(endDateTime) <= new Date(req.body.startDateTime)
        ) {
          throw new Error('End time must be after start time');
        }
        return true;
      }),
    body('isOnline').optional().isBoolean(),
  ],
  validateRequest,
  updateSchedule
);

// Delete schedule
router.delete('/:id', deleteSchedule);

export default router;
