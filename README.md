# THE ENTITY

The Entity is a 5-minute asymmetric cooperative horror game where two players must communicate to survive a rogue AI. This project explores how generative AI can create unpredictable, dynamic gameplay loops rather than relying on static, pre-written events. 

One player is trapped with the AI on a retro terminal, while the other uses a tactical tablet and a procedural digital manual to guide them safely. Because the AI personas, incident logs, and puzzles are generated on the fly, every single match is completely unique.


# Demo Video

https://github.com/user-attachments/assets/8c986b56-41a4-402f-99c8-dfe0cb8be6d1

(It makes sense when you take your time and read the manual. Pinky Promise.)

## Core Features

* **Asymmetric Co-Op:** Two players work together to extract a 3-chunk cipher from a rogue AI before Player 1 is absorbed.
* **Distinct Roles:** Player 1 (The Trapped) interacts directly with the AI terminal. Player 2 (The Operator) guides them using a tactical tablet and a digital field manual.
* **Three Dynamic Rounds:** The game progresses through the Persona Trap (social engineering), Post-Mortem Logs (flowchart deduction), and Lexical Calibration (homophone grid).
* **Symbol Lockdown Anomaly:** A high-pressure system glitch where Player 1's screen locks with abstract alien glyphs. They must describe the shapes so Player 2 can input them into a 3x3 hardware keypad.
* **High Stakes:** Every mistake, forbidden word, or failed puzzle costs a Strike, instantly draining 30 seconds from the global 5-minute timer.

## Technical Architecture

This application was engineered entirely from scratch, bypassing traditional game engines in favor of native frameworks and real-time cloud synchronization.

* **Frontend Framework:** Native Android application built with Kotlin and Jetpack Compose, utilizing Unidirectional Data Flow and Clean Architecture.
* **Graphics & Rendering:** Custom AGSL (Android Graphics Shading Language) RuntimeShaders for the CRT hardware effects, alongside Compose Canvas for skeuomorphic 3D UI elements.
* **Multiplayer Engine:** SpaceTimeDB acts as a cloud-hosted authoritative server. It uses WebSockets to achieve zero-latency state synchronization between the two isolated clients.
* **AI Infrastructure:** A custom Node and Python backend routes procedural generation requests to an LLM, enforcing strict JSON schemas for predictable game state outputs.
* **Security & Guardrails:** ArmorIQ is a proprietary real-time evaluation layer that intercepts and filters Player 1's inputs against randomized forbidden word lists before they reach the generative engine.
* **Sensory Feedback:** ElevenLabs integration provides dynamic AI voice generation, paired with deep hooks into Android's native haptic feedback constants to simulate heavy mechanical hardware interactions.

## Getting Started

### Prerequisites

* Android Studio Jellyfish or newer
* Two physical Android devices running API level 26 or higher
* Node.js v18 or higher
* SpaceTimeDB CLI installed

### Installation & Setup

1. Clone the repository.
2. Navigate to the `backend` directory and run `npm install`.
3. Start your local SpaceTimeDB instance by running `spacetime start`.
4. Publish the multiplayer module using `spacetime publish entity-server`.
5. Open the `android-client` project in Android Studio and sync Gradle.
6. Build and deploy the APK to both physical Android devices.

### How to Play

1. Launch the application on both devices.
2. Player 1 selects "Create Room" and adopts the Trapped role.
3. Player 2 selects "Join Room", enters the generated Room Code, and adopts the Operator role.
4. The 5-minute timer begins immediately upon successful connection. Do not use the forbidden words.
