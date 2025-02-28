package anas.kazay.chatapp.config;

import anas.kazay.chatapp.model.Model;
import anas.kazay.chatapp.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ModelRepository modelRepository;

    DataInitializer(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    @Override
    public void run(String... args) {
        // Initialize AI models if they don't exist yet
        if (modelRepository.count() == 0) {
            // Add Gemini model
            Model geminiModel = new Model();
            geminiModel.setName("Gemini 2.0 Flash");
            geminiModel.setDescription("Google's Gemini 2.0 Flash model for general purpose AI");
            geminiModel.setApiEndpoint("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent");
            geminiModel.setActive(true);
            modelRepository.save(geminiModel);

            // Add other models as needed
            Model gptModel = new Model();
            gptModel.setName("GPT-4");
            gptModel.setDescription("OpenAI's GPT-4 model");
            gptModel.setApiEndpoint("https://api.openai.com/v1/chat/completions");
            gptModel.setActive(true);
            modelRepository.save(gptModel);

            Model claudeModel = new Model();
            claudeModel.setName("Claude 3 Sonnet");
            claudeModel.setDescription("Anthropic's Claude 3 Sonnet model");
            claudeModel.setApiEndpoint("https://api.anthropic.com/v1/messages");
            claudeModel.setActive(true);
            modelRepository.save(claudeModel);
        }
    }
}