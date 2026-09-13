const micButton = document.getElementById("micButton");
const status = document.getElementById("status");

let mediaRecorder;
let audioChunks = [];
let stream;


// =====================================================
// MICROPHONE
// =====================================================

micButton.addEventListener("click", async () => {

    if (!mediaRecorder || mediaRecorder.state === "inactive") {

        await startRecording();

    } else {

        stopRecording();

    }

});


// =====================================================
// START RECORDING
// =====================================================

async function startRecording() {

    try {

        stream =
            await navigator.mediaDevices.getUserMedia({
                audio: true
            });

        audioChunks = [];

        mediaRecorder =
            new MediaRecorder(stream);

        mediaRecorder.ondataavailable = event => {

            if (event.data.size > 0) {

                audioChunks.push(event.data);

            }

        };

        mediaRecorder.onstop =
            sendToJarvis;

        mediaRecorder.start();

        micButton.classList.add("recording");

        status.innerText =
            "🎤 Listening... Click again to stop";

    } catch (error) {

        console.error(error);

        status.innerText =
            "❌ Microphone permission denied";
    }
}


// =====================================================
// STOP RECORDING
// =====================================================

function stopRecording() {

    mediaRecorder.stop();

    stream.getTracks().forEach(track => {
        track.stop();
    });

    micButton.classList.remove("recording");

    status.innerText =
        "🧠 Sending to JARVIS...";
}


// =====================================================
// SEND TO SPRING BOOT
// =====================================================

async function sendToJarvis() {

    const audioBlob =
        new Blob(
            audioChunks,
            {
                type: "audio/webm"
            }
        );

    const formData =
        new FormData();

    formData.append(
        "audio",
        audioBlob,
        "voice.webm"
    );


    try {

        const response =
            await fetch(
                "/api/ai/voice",
                {
                    method: "POST",
                    body: formData
                }
            );


        if (!response.ok) {

            throw new Error(
                "HTTP " + response.status
            );
        }


        const audioData =
            await response.blob();


        status.innerText =
            "🔊 JARVIS is speaking...";


        const audioUrl =
            URL.createObjectURL(
                audioData
            );


        const audio =
            new Audio(audioUrl);


        audio.onended = () => {

            status.innerText =
                "✅ Ready";

            URL.revokeObjectURL(
                audioUrl
            );
        };


        await audio.play();


    } catch (error) {

        console.error(error);

        status.innerText =
            "❌ JARVIS ERROR";
    }
}