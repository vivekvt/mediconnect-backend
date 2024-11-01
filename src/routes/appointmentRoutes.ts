import express from 'express';
import { body, query } from 'express-validator';
import { validateRequest } from '../middlewares/validation';
import {
  authenticateUser,
  isClinic,
  isPatient,
} from '../middlewares/authMiddleware';
import {
  createAppointment,
  getPatientAppointments,
  getDoctorAppointments,
  getClinicAppointments,
  getAppointmentById,
  updateAppointment,
  cancelAppointment,
  getClinicAppointmentStats,
} from '../controllers/appointmentController';

const router = express.Router();

// Validation middlewares
const createAppointmentValidation = [
  body('scheduleId').isMongoId().withMessage('Valid schedule ID is required'),
  body('patientId').isMongoId().withMessage('Valid patient ID is required'),
  body('comment').optional().trim().isString(),
];

const updateAppointmentValidation = [
  body('status')
    .optional()
    .isIn(['scheduled', 'confirmed', 'cancelled', 'completed'])
    .withMessage('Invalid status'),
  body('paymentStatus')
    .optional()
    .isIn(['pending', 'completed', 'failed', 'refunded'])
    .withMessage('Invalid payment status'),
  body('comment').optional().trim().isString(),
  body('meetingLink').optional().trim().isURL(),
  body('reports').optional().trim().isString(),
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

// Public routes (require only authentication)
router.use(authenticateUser);

// Patient specific routes
router.get(
  '/patients',
  isPatient,
  dateRangeValidation,
  validateRequest,
  getPatientAppointments
);

// Create appointment
router.post(
  '/',
  isPatient,
  createAppointmentValidation,
  validateRequest,
  createAppointment
);

// Get appointments by doctor
router.get(
  '/doctor/:doctorId',
  isClinic,
  dateRangeValidation,
  validateRequest,
  getDoctorAppointments
);

// Get appointments by clinic
router.get(
  '/clinic',
  isClinic,
  dateRangeValidation,
  validateRequest,
  getClinicAppointments
);

// Get clinic appointment statistics
router.get('/stats/clinic', isClinic, getClinicAppointmentStats);

// Update appointment
router.patch(
  '/:id',
  isClinic,
  updateAppointmentValidation,
  validateRequest,
  updateAppointment
);

// Patient Cancel appointment
router.patch('/:id/cancel', isPatient, cancelAppointment);

// Get appointment by ID
router.get('/:id', getAppointmentById);

export default router;
