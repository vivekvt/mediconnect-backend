import express, { Request, Response, NextFunction } from 'express';
import { createClient } from '@supabase/supabase-js';
import dotenv from 'dotenv';

dotenv.config();

const supabase = createClient(
  process.env.SUPABASE_URL as string,
  process.env.SUPABASE_KEY as string
);

const authRouter = express.Router();

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

  try {
    await supabase.auth.signInWithOtp({
      email,
    });

    res.status(201).json({
      message: 'OTP send to email successfully',
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

  try {
    const data = await supabase.auth.verifyOtp({
      email,
      token: otp,
      type: 'email',
    });

    res.status(201).json({
      message: 'Sign in successful',
      data,
    });
  } catch (error: any) {
    res.status(400).json({
      error: error.message,
    });
  }
});

export { authRouter, authenticateToken };
