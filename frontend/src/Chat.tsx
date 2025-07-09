import { useState } from "react";
import axios from "axios";

import ReactMarkdown from 'react-markdown';
import rehypeHighlight from 'rehype-highlight';

import 'highlight.js/styles/github.css'; 
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';


const Chat = () => {
  const [messages, setMessages] = useState<{ role: string; text: string }[]>([]);
  const [input, setInput] = useState("");

  const sendMessage = async () => {
    if (!input.trim()) return;
    const userMsg = { role: "user", text: input };
    setMessages((prev) => [...prev, userMsg]);
    setInput("");

    try {
      const res = await axios.post("http://localhost:3001/chat", { message: input });
      const botMsg = { role: "bot", text: res.data.reply };
      setMessages((prev) => [...prev, botMsg]);
    } catch (error) {
      console.error("Error talking to Gemini", error);
    }
  };

  return (

    <div className="container py-4">
      <h1 className="mb-4 text-primary">GenAI Chat</h1>
      <div className="border rounded p-3 mb-3 bg-light" style={{ height: "300px", overflowY: "auto" }}>
        {messages.map((msg, i) => (
          <div key={i} className="mb-2 text-start">
            <strong className={msg.role === "user" ? "text-success" : "text-primary"}>
              {msg.role === "user" ? "You" : "IA"}:
            </strong>{" "}
            <ReactMarkdown rehypePlugins={[rehypeHighlight]}>
              {msg.text}
            </ReactMarkdown>
          </div>
        ))}
      </div>
      <div className="input-group">
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && sendMessage()}
          className="form-control"
          placeholder="Type a message"
        />
        <button className="btn btn-primary" onClick={sendMessage}>
          Send
        </button>
      </div>
    </div>

  );
};

export default Chat;
