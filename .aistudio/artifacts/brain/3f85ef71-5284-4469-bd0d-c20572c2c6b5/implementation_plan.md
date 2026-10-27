# JARVIS AI Assistant (জার্ভিস এআই সহকারী)

An advanced futuristic personal AI assistant for Android inspired by JARVIS, combining Google Gemini AI multimodal intelligence, live real-time voice calling mode, an encrypted on-device Memory Vault for strict data privacy, camera vision scanning, device telemetry dashboard, and complete Bengali & English localization.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions were aligned during the interactive clarification phase:

- **Confirmed Decision (Language & Localization)**: Bengali (বাংলা) configured as the primary interface and voice language, with an instant one-tap toggle for English and dual-language speech recognition/synthesis.
- **Confirmed Decision (AI Engine & Privacy Architecture)**: Hybrid Cloud Gemini (via Firebase AI Logic / Gemini 3.8 Flash) for fast, intelligent reasoning and multimodal vision, coupled with a 100% on-device AES-encrypted Room Memory Vault to keep personal facts and sensitive notes private.
- **Confirmed Decision (User Profile & Avatar)**: The uploaded user photo (`/public/assets/aistudio/Picsart_26-10-12_18-45-56-182_b927d3ce.png`) is featured as the user's holographic HUD avatar badge and profile identity.
- **Security & Cloud Gate**: Google Sign-In via Firebase Auth and Jetpack Credential Manager gates cloud features, while core local assistant features remain available offline.

---

## 1. Overview & Core Concept

JARVIS is engineered to feel like a true next-generation personal operating intelligence. It brings high-tech HUD visuals, fluid voice interactions, and deep utility to everyday mobile workflows.

- **Primary Persona**: Tech enthusiasts, developers, and power users who want an intelligent voice and visual assistant with uncompromising data privacy, localized into natural Bengali.
- **Core Value Proposition**:
  - **Zero-Latency Voice & Calling HUD**: Hands-free conversational mode with animated acoustic waveforms, speech recognition, and Text-to-Speech.
  - **Multimodal Intelligence**: Live camera scanning and screenshot analysis using Gemini 3.8 Flash for instant object recognition, code debugging, and text extraction.
  - **Encrypted Memory Vault**: An offline-first, local knowledge store safeguarding private memories, routines, and user preferences with AES encryption.
  - **System Telemetry & Controls**: Live battery, memory, storage, and device hardware toggles (flashlight, audio level, network status).

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Authentication & Profile Initializer**:
   - Welcome HUD screen featuring the user's uploaded avatar enclosed in a glowing cybernetic ring.
   - One-tap Google Sign-In with Firebase Authentication or offline guest access with local vault encryption.
2. **Main JARVIS Command Center (HUD)**:
   - Central animated **Arc Reactor Core**: Pulsing concentric glowing rings and real-time audio visualization Canvas reacting to speech input/output.
   - Live status bar: AI status (Active / Standby / Thinking), battery level, network latency, and memory telemetry.
   - Quick Mode Switcher: **Chat / Coding**, **Voice Calling**, **Vision Scanner**, and **Memory Vault**.
   - Language Selector: Floating HUD switch for Bengali (বাংলা) ⟷ English (EN).
3. **Live Voice Calling HUD**:
   - Fullscreen immersive dark HUD mimicking a high-tech comms link.
   - Continuous two-way conversational loop with speech-to-text transcription and natural speech synthesis in Bengali or English.
4. **Camera Vision & Screen Scanner**:
   - CameraX / Image picker viewfinder with cybernetic targeting crosshairs.
   - Instant multimodal analysis: captures scenes, documents, or screenshots and delivers structured breakdown in Bengali.
5. **Encrypted Memory Vault**:
   - Visual list of memories categorized into "Personal Facts", "Work & Code", and "Secrets / Encrypted".
   - Searchable, taggable, with pin/lock protection and local SQLite persistence.

### Visual Identity & Theme

- **Aesthetic Direction**: Futuristic Sci-Fi / Cybernetic Tactical HUD (Iron Man Mark LXXXV & Cyberpunk aesthetic).
- **Color Palette**:
  - Cosmic Black (`#050811` / `--background`): Deep space obsidian background.
  - Cyber Navy Surface (`#0D1527` / `--surface`): Card and panel container backgrounds.
  - Arc Cyan (`#00F0FF` / `--primary`): Radiant neon cyan for primary buttons, reactor rings, and active states.
  - Cobalt Glow (`#1D70B8` / `--secondary`): Deep holographic blue for gradients and borders.
  - Alert Amber / Plasma Orange (`#FF9F1C` / `--accent`): Telemetry warnings and highlight badges.
  - Laser Green (`#00E676` / `--tertiary`): System online and hardware sensor indicators.
- **Typography**: Clean, high-tech sans-serif typography with monospace numeric telemetry readouts, styled for Bengali script legibility.
- **Motion & Feedback**: Smooth infinite rotation of reactor rings, canvas spring audio visualizer, ripple effects on HUD chips, and glowing pulse animations on voice activation.

---

## 3. Key Product Decisions & Trade-Offs

- **Hybrid Intelligence vs. Cloud Only**:
  - *Chosen Approach*: Cloud Gemini 3.8 Flash for high-level reasoning and vision, paired with local Room database for Memory Vault and offline rule-based fallback responses.
  - *Rationale*: Guarantees blazing-fast offline availability for device controls and private data while providing cutting-edge multimodal intelligence when online.
- **Local AES Encryption for Memory Vault**:
  - *Chosen Approach*: User notes and memories stored in encrypted form in Room with an Android Keystore-derived key.
  - *Rationale*: Meets the user's explicit request for local data privacy and security.
- **Android SpeechRecognizer & TTS**:
  - *Chosen Approach*: Native Android `SpeechRecognizer` API and `TextToSpeech` engine configured with `Locale("bn", "BD")` and fallback `Locale.US`.
  - *Rationale*: Low latency, zero third-party audio service costs, and offline-compatible speech synthesis.

---

## 4. Technical Architecture & Data Strategy

### System Architecture Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                    JARVIS Android App                        │
├──────────────────────────────────────────────────────────────┤
│  UI Layer (Jetpack Compose M3)                               │
│  ┌─────────────────┐  ┌──────────────────┐  ┌─────────────┐ │
│  │ Main HUD View   │  │ Voice Calling    │  │ Vision View │ │
│  │ (Reactor Canvas)│  │ (Live Comms HUD) │  │ (CameraX)   │ │
│  └────────┬────────┘  └────────┬─────────┘  └──────┬──────┘ │
│           │                    │                   │         │
│           └───────────┬────────┴───────────────────┘         │
│                       ▼                                      │
│  State & ViewModel Layer                                     │
│  ┌────────────────────────────────────────────────────────┐  │
│  │ JarvisViewModel (Chat, Telemetry, Language, UI State)  │  │
│  │ VoiceCallViewModel (STT, TTS, Audio Visualizer State)  │  │
│  │ MemoryVaultViewModel (Encrypted CRUD, Search)          │  │
│  └────────────────────┬───────────────────────────────────┘  │
│                       │                                      │
│  Service & Repository Layer                                  │
│  ┌───────────────────┐ ┌──────────────────┐ ┌──────────────┐ │
│  │ GeminiAiService   │ │ VoiceService     │ │ Hardware     │ │
│  │ (Firebase AI /    │ │ (SpeechRecognizer│ │ Telemetry    │ │
│  │  Gemini 3.8 Flash)│ │  & Android TTS)  │ │ Manager      │ │
│  └─────────┬─────────┘ └──────────────────┘ └──────────────┘ │
│            │                                                 │
│  Data & Persistence Layer                                    │
│  ┌────────────────────────────────────────────────────────┐  │
│  │ Encrypted MemoryVault (Room Database + Keystore AES)   │  │
│  │ Firebase Auth (Google Sign-In Credential Manager)     │  │
│  │ Language & Preferences DataStore                       │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

### Data Model & State

1. **`JarvisMessage`**:
   - `id: String`, `sender: MessageSender (USER | JARVIS | SYSTEM)`, `text: String`, `timestamp: Long`, `isCode: Boolean`, `codeLanguage: String?`, `imageUri: String?`
2. **`VaultMemory`**:
   - `id: Long`, `title: String`, `encryptedContent: String`, `category: String (PERSONAL | WORK | CODING | SYSTEM)`, `createdAt: Long`, `isPinned: Boolean`
3. **`TelemetryMetrics`**:
   - `batteryPercent: Int`, `isCharging: Boolean`, `cpuUsagePercent: Int`, `ramAvailableMb: Long`, `storagePercent: Int`, `networkType: String`, `systemHealth: String`
4. **`JarvisLanguage`**:
   - `BENGALI ("bn")`, `ENGLISH ("en")`

### Verification & Testing Plan

1. **Compilation Check**: Run `compile_applet` to ensure zero compilation or build errors across Compose and dependencies.
2. **Voice & TTS Verification**: Verify SpeechRecognizer launcher flows and TTS initialization gracefully handle permission states.
3. **Vault Encryption Verification**: Validate Room database encryption helpers and CRUD state management.
4. **Multimodal & Camera Verification**: Confirm image loading via Coil and Gemini content generation handlers.
