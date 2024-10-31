import dotenv from 'dotenv';
import mongoose from 'mongoose';

dotenv.config();

const MONGODB_URL = process.env.MONGODB_URL || '';

export const dbConnect = async () => {
  try {
    if (!MONGODB_URL) {
      throw new Error('MONGODB_URL not found in environment variables');
    }

    // Log the MongoDB URL with sensitive information redacted
    const redactedUrl = MONGODB_URL.replace(
      /mongodb(\+srv)?:\/\/(.*):(.*)@/,
      'mongodb$1://**redacted**:**redacted**@'
    );
    console.log('Attempting to connect to MongoDB at:', redactedUrl);

    // Disconnect if already connected
    if (mongoose.connection.readyState !== 0) {
      console.log('Disconnecting from existing MongoDB connection...');
      await mongoose.disconnect();
    }

    // Connection options
    const mongooseOptions = {
      serverSelectionTimeoutMS: 5000, // Timeout after 5 seconds
      socketTimeoutMS: 45000, // Close sockets after 45 seconds of inactivity
      family: 4, // Use IPv4, skip trying IPv6
      retryWrites: true,
      maxPoolSize: 10,
      minPoolSize: 1,
    };

    // Attempt connection
    await mongoose.connect(MONGODB_URL, mongooseOptions);

    // Set up connection error handlers
    mongoose.connection.on('error', (err) => {
      console.error('MongoDB connection error:', err);
    });

    mongoose.connection.on('disconnected', () => {
      console.log('MongoDB disconnected');
    });

    mongoose.connection.on('connected', () => {
      console.log('MongoDB connected successfully');
    });

    console.log('Successfully connected to MongoDB');
  } catch (error) {
    console.error('MongoDB connection error details:', {
      error: error instanceof Error ? error.message : String(error),
      stack: error instanceof Error ? error.stack : undefined,
      mongoUrl: MONGODB_URL ? 'URL provided' : 'URL missing',
    });
    throw error; // Re-throw to be handled by the caller
  }
};
