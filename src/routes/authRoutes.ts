import express from 'express';
import { getOtp, verifyOtp, getProfile } from '../controllers/authController';
import { authenticateUser } from '../middlewares/authMiddleware';
import { validateRequest } from '../middlewares/validation';
import { body } from 'express-validator';

const router = express.Router();

// Validation middleware
const otpValidation = [
  body('email').isEmail().normalizeEmail(),
  body('role').isIn(['clinic', 'patient']),
];

const verifyOtpValidation = [
  body('email').isEmail().normalizeEmail(),
  body('otp').isLength({ min: 6, max: 6 }).isNumeric(),
];

// Routes
router.post('/get-otp', otpValidation, validateRequest, getOtp);
router.post('/verify-otp', verifyOtpValidation, validateRequest, verifyOtp);
router.get('/profile', authenticateUser, getProfile);

export default router;
