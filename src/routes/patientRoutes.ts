import express from 'express';
import { body } from 'express-validator';
import { validateRequest } from '../middlewares/validation';
import { authenticateUser, isPatient } from '../middlewares/authMiddleware';
import { updatePatientProfile } from '../controllers/patientController';

const router = express.Router();

const patientProfileValidation = [
  body('firstName').optional().trim().notEmpty(),
  body('lastName').optional().trim().notEmpty(),
  body('email').optional().isEmail().normalizeEmail(),
  body('phone')
    .optional()
    .matches(/^\+?[\d\s-]{10,}$/),
  body('address').optional().trim().notEmpty(),
  body('gender').optional().isIn(['male', 'female', 'other']),
  body('dateOfBirth').optional().isISO8601(),
];

router.use(authenticateUser, isPatient);

router.get('/profile', authenticateUser, isPatient, updatePatientProfile);

router.patch(
  '/profile',
  authenticateUser,
  isPatient,
  patientProfileValidation,
  validateRequest,
  updatePatientProfile
);

export default router;
