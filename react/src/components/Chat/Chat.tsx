import { useState } from "react";
import axios from "axios";

import ReactMarkdown from 'react-markdown';
import rehypeHighlight from 'rehype-highlight';

interface ChatProps {
  client: string;
  url: string;
}
interface Model {
  id: string;
  label: string;
}

const models: Model[] = [
  { id: 'vertex', label: 'Vertex' },
  { id: 'openai', label: 'OpenAI' },
  { id: 'gemini', label: 'Gemini' },
];

const Chat = (props: ChatProps) => {
  const [messages, setMessages] = useState<{ role: string; text: string }[]>([]);
  const [input, setInput] = useState("");

  const [selectedModel, setSelectedModel] = useState<string>(props.client);

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSelectedModel(event.target.value);
  };

  const sendMessage = async () => {
    console.log(props);
    if (!input.trim()) return;
    const userMsg = { role: "user", text: input };
    setMessages((prev) => [...prev, userMsg]);
    setInput("");

    try {
      let URL = props.url + "?client=" + selectedModel;
      const res = await axios.post(URL, { input });
      const botMsg = { role: "bot", text: res.data };
      setMessages((prev) => [...prev, botMsg]);
    } catch (error) {
      console.error("Error talking to LLM", error);
    }
  };

  return (
    <div className="container py-4">
      <div >
        <h3>Select a model:</h3>
        <div style={{ display: 'flex', gap: '20px' }}>
          {models.map((model) => (
            <label key={model.id} style={{ display: 'block', marginBottom: '8px' }}>
              <input
                type="radio"
                name="model"
                value={model.id}
                checked={selectedModel === model.id}
                onChange={handleChange}
              />
              {' '}{model.label}
            </label>
          ))}
        </div>
      </div>

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
