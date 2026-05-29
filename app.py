from flask import Flask, request, jsonify
from chatbot_engine import GeminiChatbot

app = Flask(__name__)

# 챗봇 엔진 초기화
chatbot = GeminiChatbot()

@app.route('/chat', methods=['POST'])
def chat_endpoint():
    """
    Spring Boot ↔ Python 연동 엔드포인트

    요청 (Spring ChatbotService → Python):
    {
        "message": "사용자 입력",
        "history": [
            {"role": "user",  "content": "이전 질문"},
            {"role": "bot",   "content": "이전 답변"}
        ]
    }

    응답 (Python → Spring ChatbotDto.PythonResponse):
    {
        "answer": "챗봇 답변"
    }
    """
    data = request.get_json()

    if not data or 'message' not in data:
        return jsonify({"error": "Missing 'message' field"}), 400

    current_message = data.get('message')
    history = data.get('history', [])

    bot_reply = chatbot.get_response(current_message, history)

    return jsonify({
        "answer": bot_reply
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
