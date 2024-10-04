import dotenv from 'dotenv';
dotenv.config();
import express from 'express';
import cors from 'cors';
import morgan from 'morgan';
import bodyParser from 'body-parser';
import { authRouter } from './controllers/authController';

const app = express();
const PORT = process.env.PORT || 8080;

app.use(morgan('combined'));

app.use(cors());

// parse body application/json
app.use(bodyParser.json());

// Use the authRouter for all routes starting with /auth
app.use('/auth', authRouter);

app.get('/', (req, res) => {
  res.send('MediConnect Backend');
});

app.listen(PORT, () => {
  console.log(
    `MediConnect Backend App is listening on port http://localhost:${PORT}`
  );
});
