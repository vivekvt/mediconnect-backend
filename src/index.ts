import dotenv from 'dotenv';
dotenv.config();
import express from 'express';
import cors from 'cors';
import morgan from 'morgan';
import bodyParser from 'body-parser';
import authRoutes from './routes/authRoutes';
import clinicRoutes from './routes/clinicRoutes';
import patientRoutes from './routes/patientRoutes';
import { dbConnect } from './utils/dbConnect';
import { uploadFile } from './controllers/fileUploadController';

const app = express();
const PORT = process.env.PORT || 8080;

// Middleware
app.use(morgan('combined'));
app.use(cors());
app.use(bodyParser.json());

app.post('/file-upload', uploadFile);

// Routes
app.use('/auth', authRoutes);
app.use('/clinic', clinicRoutes);
app.use('/patient', patientRoutes);

app.get('/', (req, res) => {
  res.send('MediConnect Backend');
});

// Connect to database and start server
const startServer = async () => {
  try {
    await dbConnect();
    app.listen(PORT, () => {
      console.log(
        `MediConnect Backend App is listening on port http://localhost:${PORT}`
      );
    });
  } catch (error) {
    console.error('Failed to connect to database:', error);
    process.exit(1);
  }
};

startServer();
