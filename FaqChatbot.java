import java.util.*;

public class FaqChatbot {

    static class Intent {
        Set<String> keywords;
        String answer;

        Intent(String answer, String... keywords) {
            this.answer = answer;
            this.keywords = new HashSet<>(Arrays.asList(keywords));
        }
    }

    static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "the", "a", "an", "is", "are", "do", "does", "you", "i", "to", "of",
            "and", "what", "how", "can", "my", "me", "your", "it", "in", "on", "for", "please"));

    static List<Intent> intents = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadFaq();
        System.out.println("Bot: Hi! Ask me anything. Type 'bye' to exit.");

        while (true) {
            System.out.print("You: ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) continue;

            List<String> tokens = tokenize(input);
            if (tokens.contains("bye") || tokens.contains("exit") || tokens.contains("quit")) {
                System.out.println("Bot: Goodbye! Have a great day.");
                break;
            }

            Intent best = findBestIntent(tokens);
            if (best != null) {
                System.out.println("Bot: " + best.answer);
            } else {
                learn(tokens);
            }
        }
    }

    // NLP step: lowercase -> remove punctuation -> split -> remove stop words -> simple stemming
    static List<String> tokenize(String text) {
        String clean = text.toLowerCase().replaceAll("[^a-z0-9 ]", " ");
        List<String> tokens = new ArrayList<>();
        for (String w : clean.split("\\s+")) {
            if (w.isEmpty() || STOP_WORDS.contains(w)) continue;
            tokens.add(stem(w));
        }
        return tokens;
    }

    static String stem(String w) {
        if (w.length() > 4 && w.endsWith("ing")) return w.substring(0, w.length() - 3);
        if (w.length() > 3 && w.endsWith("s")) return w.substring(0, w.length() - 1);
        return w;
    }

    // Rule-based matching: the intent with the most matching keywords wins
    static Intent findBestIntent(List<String> tokens) {
        Intent best = null;
        int bestScore = 0;
        for (Intent in : intents) {
            int score = 0;
            for (String t : tokens) {
                if (in.keywords.contains(t)) score++;
            }
            if (score > bestScore) {
                bestScore = score;
                best = in;
            }
        }
        return best;
    }

    // "Training": if the bot doesn't know, the user can teach it a new answer
    static void learn(List<String> tokens) {
        System.out.println("Bot: I don't know that yet. Type the answer to teach me (or press Enter to skip):");
        System.out.print("You (teach): ");
        String answer = sc.nextLine().trim();
        if (answer.isEmpty() || tokens.isEmpty()) {
            System.out.println("Bot: OK, maybe next time.");
            return;
        }
        intents.add(new Intent(answer, tokens.toArray(new String[0])));
        System.out.println("Bot: Thanks, I learned something new!");
    }

    static void loadFaq() {
        intents.add(new Intent("Hello! How can I help you today?", "hello", "hi", "hey", "morning", "evening"));
        intents.add(new Intent("I'm a simple Java chatbot built for the CodeAlpha internship.", "name", "who", "bot"));
        intents.add(new Intent("We are open Sunday to Thursday, 9 AM to 5 PM.", "hour", "open", "close", "time", "working"));
        intents.add(new Intent("Our prices start at $10. Check the pricing page for details.", "price", "cost", "much", "pay", "payment"));
        intents.add(new Intent("You can reach us at support@example.com.", "contact", "email", "support", "reach", "phone"));
        intents.add(new Intent("You can return any product within 14 days for a full refund.", "refund", "return", "money", "back"));
        intents.add(new Intent("Delivery takes 3 to 5 working days.", "shipping", "delivery", "deliver", "ship", "arrive"));
        intents.add(new Intent("You can ask about hours, prices, delivery, refunds, or contact info.", "help", "assist", "service"));
        intents.add(new Intent("You're welcome!", "thanks", "thank", "appreciate"));
    }
}
