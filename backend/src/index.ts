import express, { Request, Response } from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import { getGeminiReply } from './gemini';

dotenv.config();
const app = express();
app.use(cors());
app.use(express.json());

app.post('/chat', async (req: Request, res: Response) => {
  const { message } = req.body;
  try {
    const reply = await getGeminiReply(message);
    res.json({ reply });
  } catch (err) {
    res.status(500).json({ error: 'Failed to get Gemini response' });
  }
});

const PORT = 3001;
app.listen(PORT, () => console.log(`Backend running on http://localhost:${PORT}`));
