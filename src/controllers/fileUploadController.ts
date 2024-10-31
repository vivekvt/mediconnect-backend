import { Request, Response } from 'express';
import { createClient } from '@supabase/supabase-js';
import multer from 'multer';
import { v4 as uuidv4 } from 'uuid';
import path from 'path';

// Types
interface FileUploadResponse {
  message: string;
  fileName: string;
  fileUrl: string;
}

interface ErrorResponse {
  error: string;
  details?: string;
}

interface MulterRequest extends Request {
  file?: Express.Multer.File;
}

// Define allowed MIME types as a const type
const ALLOWED_MIME_TYPES = [
  'image/jpeg',
  'image/png',
  'image/gif',
  'application/pdf',
] as const;

// Create a type from the array
type AllowedMimeType = (typeof ALLOWED_MIME_TYPES)[number];

// Environment variables type checking
if (!process.env.SUPABASE_URL || !process.env.SUPABASE_KEY) {
  throw new Error('Missing required environment variables');
}

// Initialize Supabase client
const supabase = createClient(
  process.env.SUPABASE_URL,
  process.env.SUPABASE_KEY
);

// Configure multer for memory storage
const storage = multer.memoryStorage();
const upload = multer({
  storage,
  limits: {
    fileSize: 5 * 1024 * 1024, // 5MB limit
  },
});

class FileUploadError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'FileUploadError';
  }
}

// Constants for configuration
export const CONFIG = {
  ALLOWED_MIME_TYPES,
  MAX_FILE_SIZE: 5 * 1024 * 1024, // 5MB
  BUCKET_NAME: 'mediconnect-bucket',
} as const;

const uploadFile = async (
  req: MulterRequest,
  res: Response<FileUploadResponse | ErrorResponse>
): Promise<void> => {
  try {
    await new Promise<void>((resolve, reject) => {
      upload.single('file')(req, res, (err) => {
        if (err) reject(new FileUploadError(err.message));
        resolve();
      });
    });

    if (!req.file) {
      res.status(400).json({ error: 'No file provided' });
      return;
    }

    // Type guard for MIME type
    if (!isSupportedMimeType(req.file.mimetype)) {
      res.status(400).json({
        error: 'Invalid file type',
        details: `Allowed types are: ${ALLOWED_MIME_TYPES.join(', ')}`,
      });
      return;
    }

    const fileExtension = path.extname(req.file.originalname);
    const fileName = `${uuidv4()}${fileExtension}`;
    const bucketName = CONFIG.BUCKET_NAME;
    const filePath = `uploads/${fileName}`;

    // Upload file to Supabase Storage
    const { error: uploadError } = await supabase.storage
      .from(bucketName)
      .upload(filePath, req.file.buffer, {
        contentType: req.file.mimetype,
        cacheControl: '3600',
        upsert: false,
      });

    if (uploadError) {
      throw new FileUploadError(uploadError.message);
    }

    // Get public URL
    const {
      data: { publicUrl },
    } = supabase.storage.from(bucketName).getPublicUrl(filePath);

    res.status(200).json({
      message: 'File uploaded successfully',
      fileName,
      fileUrl: publicUrl,
    });
  } catch (error) {
    const errorMessage =
      error instanceof Error ? error.message : 'Unknown error occurred';
    res.status(500).json({
      error: 'Upload failed',
      details: errorMessage,
    });
  }
};

// Type guard function
function isSupportedMimeType(mimetype: string): mimetype is AllowedMimeType {
  return ALLOWED_MIME_TYPES.includes(mimetype as AllowedMimeType);
}

// Middleware to validate file type
export const validateFileType = (
  req: MulterRequest,
  res: Response,
  next: () => void
): void => {
  if (!req.file) {
    next();
    return;
  }

  if (!isSupportedMimeType(req.file.mimetype)) {
    res.status(400).json({
      error: 'Invalid file type',
      details: `Allowed types are: ${ALLOWED_MIME_TYPES.join(', ')}`,
    });
    return;
  }

  next();
};

export { uploadFile };
