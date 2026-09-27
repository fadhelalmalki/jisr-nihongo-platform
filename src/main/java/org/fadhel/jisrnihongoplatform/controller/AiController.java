package org.fadhel.jisrnihongoplatform.controller;

import lombok.RequiredArgsConstructor;
import org.fadhel.jisrnihongoplatform.service.OpenRouterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final OpenRouterService openRouterService;


    // Extra Endpoint: 1 outOf Japanese Kanji Explanation Helper
    @GetMapping("/explain-kanji")
    public ResponseEntity<Map<String, String>> explainKanji(@RequestParam String kanji) {
        String prompt = "Explain the Japanese character or word '" + kanji + "' for a student. Include its Onyomi, Kunyomi, JLPT level, and 2 example sentences with English translations.";
        String explanation = openRouterService.askAi(prompt);
        return ResponseEntity.status(200).body(Map.of("kanji", kanji, "explanation", explanation));
    }

    // Extra Endpoint: 2 outOf Japanese Practice Quiz Generator
    @GetMapping("/generate-quiz")
    public ResponseEntity<Map<String, String>> generateQuiz(@RequestParam(defaultValue = "N5") String level) {
        String prompt = "Generate a 3-question multiple choice Japanese grammar quiz for JLPT " + level + " with answer keys at the bottom.";
        String quiz = openRouterService.askAi(prompt);
        return ResponseEntity.status(200).body(Map.of("level", level, "quiz", quiz));
    }

    // Extra Endpoint: 3 outOf Japanese Sentence & Grammar Checker
    @PostMapping("/check-grammar")
    public ResponseEntity<Map<String, String>> checkGrammar(@RequestBody Map<String, String> body) {
        String sentence = body.get("sentence");
        if (sentence == null || sentence.trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Sentence cannot be empty"));
        }

        String prompt = "Act as a strict Japanese language teacher. Analyze this Japanese sentence: '" + sentence + "'. " +
                "Provide: 1) Natural/Corrected version, 2) Formality level (Casual, Teineigo, Keigo), " +
                "3) Detailed explanation of any mistakes or improvements in English.";

        String result = openRouterService.askAi(prompt);
        return ResponseEntity.status(200).body(Map.of("originalSentence", sentence, "analysis", result));
    }

    // Extra Endpoint: 4 outOf Conversational Roleplay Scenario
    @GetMapping("/dialogue")
    public ResponseEntity<Map<String, String>> generateDialogue(
            @RequestParam(defaultValue = "restaurant") String scenario,
            @RequestParam(defaultValue = "N5") String level) {

        String prompt = "Generate a realistic 4-line Japanese dialogue for a student at JLPT " + level + " level for the scenario: '" + scenario + "'. " +
                "Format each line with Japanese (Kanji/Kana), Romaji, and English translation.";

        String dialogue = openRouterService.askAi(prompt);
        return ResponseEntity.status(200).body(Map.of("scenario", scenario, "level", level, "dialogue", dialogue));
    }

    // Extra Endpoint: 5 outOf Themed Vocabulary Generator
    @GetMapping("/themed-vocab")
    public ResponseEntity<Map<String, String>> getThemedVocab(
            @RequestParam(defaultValue = "general") String theme,
            @RequestParam(defaultValue = "N5") String level) {

        String prompt = "List 5 essential JLPT " + level + " vocabulary words related to the theme '" + theme + "'. " +
                "For each word, include: Kanji/Kana, Romaji, English meaning, and 1 short example sentence with translation.";

        String vocabList = openRouterService.askAi(prompt);
        return ResponseEntity.status(200).body(Map.of("theme", theme, "level", level, "vocabulary", vocabList));
    }

    // Extra Endpoint: 6 outOf Japanese Cultural Etiquette Guide
    @GetMapping("/culture-guide")
    public ResponseEntity<Map<String, String>> getCultureGuide(@RequestParam String topic) {
        String prompt = "Explain 3 key Japanese cultural rules or etiquette tips regarding '" + topic + "'. " +
                "Include relevant Japanese vocabulary words (like 'Itadakimasu' or 'Ojikigi') with their meanings.";

        String guide = openRouterService.askAi(prompt);
        return ResponseEntity.status(200).body(Map.of("topic", topic, "guide", guide));
    }

    // Extra Endpoint: 7 outOf to generate AI text through custom prompt
    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateAiText(@RequestBody Map<String, String> body) {
        String prompt = body.get("prompt");
        if (prompt == null || prompt.trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Prompt cannot be empty"));
        }

        String aiResponse = openRouterService.askAi(prompt);
        return ResponseEntity.status(200).body(Map.of("response", aiResponse));
    }

    // Extra Endpoint: 8 outOf Arabic-Japanese Cultural & Phrase Bridge
    @PostMapping("/arabic-bridge")
    public ResponseEntity<Map<String, String>> translateArabicConcept(@RequestBody Map<String, String> body) {
        String arabicPhrase = body.get("phrase");
        if (arabicPhrase == null || arabicPhrase.trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Phrase cannot be empty"));
        }

        String prompt = "Explain the closest Japanese equivalent to the Arabic phrase or concept: '" + arabicPhrase + "'. " +
                "Provide Japanese expression in Kanji/Kana, Romaji, literal translation, and cultural context comparing how both cultures express this concept.";

        String explanation = openRouterService.askAi(prompt);
        return ResponseEntity.status(200).body(Map.of("arabicPhrase", arabicPhrase, "japaneseBridge", explanation));
    }

    // Extra Endpoint: 9 outOf to convert informal Japanese into polite/business Japanese
    @PostMapping("/keigo-converter")
    public ResponseEntity<Map<String, String>> convertToKeigo(@RequestBody Map<String, String> body) {
        String sentence = body.get("sentence");
        if (sentence == null || sentence.trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Sentence cannot be empty"));
        }

        String prompt = "Convert the following Japanese sentence into both Sonkeigo (Respectful Language) and Kenjougo (Humble Language): '" + sentence + "'. " +
                "Provide: 1) Sonkeigo version with English translation, 2) Kenjougo version with English translation, " +
                "3) A brief explanation of when to use each in a business setting.";

        String result = openRouterService.askAi(prompt);
        return ResponseEntity.status(200).body(Map.of("originalSentence", sentence, "keigoBreakdown", result));
    }

    // Extra Endpoint: 10 outOf Kanji Radical Breakdown Generator
    @GetMapping("/kanji-radicals")
    public ResponseEntity<Map<String, String>> getKanjiRadicals(@RequestParam String kanji) {
        String prompt = "Break down the Kanji character '" + kanji + "' into its component radicals. " +
                "Return ONLY the radicals, their symbols, and their meanings. " +
                "Do not include any mnemonic story, intro, or extra commentary.";

        String radicals = openRouterService.askAi(prompt);
        return ResponseEntity.ok(Map.of("kanji", kanji, "radicals", radicals));
    }

    // Extra Endpoint: 11 JLPT Study Plan Generator
    @GetMapping("/study-plan")
    public ResponseEntity<Map<String, String>> generateStudyPlan(
            @RequestParam(defaultValue = "N5") String level,
            @RequestParam(defaultValue = "4") Integer weeks,
            @RequestParam(defaultValue = "grammar") String focus) {

        String prompt = "Create a structured " + weeks + "-week study plan for a student preparing for JLPT " + level + ". " +
                "The student wants to focus especially on '" + focus + "'. Provide weekly actionable goals, recommended daily habits, and practice strategies.";

        String plan = openRouterService.askAi(prompt);
        return ResponseEntity.status(200).body(Map.of("level", level, "durationWeeks", String.valueOf(weeks), "focusArea", focus, "studyPlan", plan));
    }

    // Extra Endpoint: 12 Reading Comprehension Generator
    @GetMapping("/reading-passage")
    public ResponseEntity<Map<String, String>> generateReadingPassage(
            @RequestParam(defaultValue = "N5") String level,
            @RequestParam(defaultValue = "daily life") String topic) {

        String prompt = "Write a short 100-word Japanese reading passage for JLPT " + level + " students about '" + topic + "'. " +
                "Provide: 1) Japanese text with Furigana in parentheses, 2) English translation, " +
                "3) 2 multiple-choice comprehension questions with an answer key at the end.";

        String passage = openRouterService.askAi(prompt);
        return ResponseEntity.status(200).body(Map.of("level", level, "topic", topic, "content", passage));
    }

    // Extra Endpoint: 13 Instructor Lesson Plan Builder
    @PostMapping("/lesson-plan")
    public ResponseEntity<Map<String, String>> generateLessonPlan(@RequestBody Map<String, String> body) {
        String topic = body.get("topic");
        String level = body.getOrDefault("level", "N5");

        if (topic == null || topic.trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Topic cannot be empty"));
        }

        String prompt = "Act as an expert curriculum designer. Create a 45-minute Japanese lesson plan for teaching '" + topic + "' at JLPT " + level + " level. " +
                "Include: 1) Warm-up activity (5 min), 2) Grammar/Vocab presentation (15 min), " +
                "3) Interactive practice activity (15 min), 4) Wrap-up quiz (10 min).";

        String lessonPlan = openRouterService.askAi(prompt);
        return ResponseEntity.status(200).body(Map.of("topic", topic, "level", level, "lessonPlan", lessonPlan));
    }

}
