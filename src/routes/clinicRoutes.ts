// routes/clinicRoutes.ts
import express from 'express';
import { body } from 'express-validator';
import { validateRequest } from '../middlewares/validation';
import { authenticateUser, isClinic } from '../middlewares/authMiddleware';
import {
  createDoctor,
  getAllDoctors,
  getDoctorById,
  updateDoctor,
  deleteDoctor,
  updateClinicProfile,
  getClinicProfile,
  getClinicByClinicCode,
  getAllDoctorByClinicId,
} from '../controllers/clinicController';

const router = express.Router();

const doctorValidation = [
  body('firstName').trim().notEmpty().withMessage('First name is required'),
  body('lastName').trim().notEmpty().withMessage('Last name is required'),
  body('specialty').trim().notEmpty().withMessage('Specialty is required'),
];

const clinicProfileValidation = [
  body('name').optional().trim().notEmpty(),
  body('email').optional().isEmail().normalizeEmail(),
  body('phone')
    .optional()
    .matches(/^\+?[\d\s-]{10,}$/),
  body('address').optional().notEmpty(),
];

router.get('/clinic-code/:clinicCode', getClinicByClinicCode);

router.get('/doctors/clinic-id/:clinicId', getAllDoctorByClinicId);

// Apply clinic authentication to all doctor routes
router.use(authenticateUser, isClinic);

router.get('/profile', getClinicProfile);

router.patch(
  '/profile',
  authenticateUser,
  isClinic,
  clinicProfileValidation,
  validateRequest,
  updateClinicProfile
);

router.post('/doctors', doctorValidation, validateRequest, createDoctor);
router.get('/doctors', getAllDoctors);
router.get('/doctors/:id', getDoctorById);
router.patch('/doctors/:id', doctorValidation, validateRequest, updateDoctor);
router.delete('/doctors/:id', deleteDoctor);

export default router;
