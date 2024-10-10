import express, { Request, Response, NextFunction } from 'express';
import { createClient } from '@supabase/supabase-js';

const supabase = createClient(
  process.env.SUPABASE_URL as string,
  process.env.SUPABASE_KEY as string
);

const authRouter = express.Router();

// Email validation function
const validateEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

// Middleware to check JWT
const authenticateToken = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];

  if (token == null) return res.sendStatus(401);

  try {
    const { data, error } = await supabase.auth.getUser(token);
    if (error) throw error;
    (req as any).user = data.user;
    next();
  } catch (error) {
    return res.sendStatus(403);
  }
};

// Signup route
authRouter.post('/signin-with-otp', async (req: Request, res: Response) => {
  const { email } = req.body;

  if (!validateEmail(email)) {
    return res.status(400).json({
      error: 'Invalid email format',
    });
  }

  try {
    const data = await supabase.auth.signInWithOtp({
      email,
    });

    if (data?.error?.code) {
      throw new Error(data?.error?.code);
    }

    res.status(201).json({
      message: 'OTP sent to email successfully',
      email,
    });
  } catch (error: any) {
    res.status(400).json({
      error: error.message,
    });
  }
});

authRouter.post('/signin-verify-otp', async (req: Request, res: Response) => {
  const { email, otp } = req.body;

  if (!validateEmail(email)) {
    return res.status(400).json({
      error: 'Invalid email format',
    });
  }

  try {
    const data = await supabase.auth.verifyOtp({
      email,
      token: otp,
      type: 'email',
    });

    if (data?.error?.code) {
      throw new Error(data?.error?.code);
    }

    res.status(201).json({
      message: 'Sign in successful',
      data: data?.data,
    });
  } catch (error: any) {
    res.status(400).json({
      error: error.message,
    });
  }
});

export { authRouter, authenticateToken };
