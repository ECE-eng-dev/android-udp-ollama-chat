import socket
import requests

PORT = 9999
OLLAMA_URL = "http://127.0.0.1:11434/api/generate"
MODEL = "tinyllama"   # or phi or llama3.2 if RAM allows

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind(("0.0.0.0", PORT))

print(f"Ollama UDP AI Server running on port {PORT}")

while True:
    data, addr = sock.recvfrom(4096)
    prompt = data.decode().strip()

    print(f"Question from {addr}: {prompt}")

    try:
        res = requests.post(
            OLLAMA_URL,
            json={
                "model": MODEL,
                "prompt": prompt,
                "stream": False
            },
            timeout=120
        )

        reply = res.json().get("response", "No response")

    except Exception as e:
        reply = f"Error: {e}"

    sock.sendto(reply.encode(), addr)

