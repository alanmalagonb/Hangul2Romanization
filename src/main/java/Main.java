import org.junit.jupiter.api.Test;

public class Main {

    private final static String getCharacter(final String character) {
        // the following characters are in the correct (i.e. Unicode) order
        final String initials = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";// list of initials
        final String vowels = "ᅡᅢᅣᅤᅥᅦᅧᅨᅩᅪᅫᅬᅭᅮᅯᅰᅱᅲᅳᅴᅵ";// list of vowels
        final String finals = "ᆨᆩᆪᆫᆬᆭᆮᆯᆰᆱᆲᆳᆴᆵᆶᆷᆸᆹᆺᆻᆼᆽᆾᆿᇀᇁᇂ";// list of tail characters

        final int characterValue = character.codePointAt(0); // Unicode value

        // Hangul syllabary occupies the Unicode range from
        // AC00 (decimal 44032) to D7A3 (decimal 55171)
        final int hangulUnicodeStartValue = 44032;

        if (characterValue < hangulUnicodeStartValue)
            return character; // for instance for 32 (space)

        // 44620 - 44620 = 588
        // number of characters beginning with any particular initial

        // 27 tail consonants + 1 (nothing) = 28

        // tail = mod(Hangul codepoint - 44032, 28)
        int tailIndex = Math.round((characterValue - hangulUnicodeStartValue) % 28) - 1;

        // vowel = 1 + mod(Hangul codepoint - 44032 - tail,588) / 28
        final int vowelIndex = Math.round(((characterValue - hangulUnicodeStartValue - tailIndex) % 588) / 28);

        // lead = 1 + int[(Hangul codepoint - 44032) / 588]
        final int initialIndex = (characterValue - hangulUnicodeStartValue) / 588;

        final String leadString = initials.substring(initialIndex, initialIndex + 1);
        final String vowelString = vowels.substring(vowelIndex, vowelIndex + 1);
        final String tailString = tailIndex == -1 ? "" : finals.substring(tailIndex, tailIndex + 1);// may be -1 when there is no tail character

        tailIndex = tailIndex == -1 ? 0 : tailIndex + 1;
        final int reconstructKoreanCode = tailIndex + (vowelIndex)*28 + (initialIndex)*588 + hangulUnicodeStartValue;
        final String reconstructKoreanCharacter = Character.toString(reconstructKoreanCode);

        return leadString + vowelString + tailString;
    }

    public final static String romanizeKoreanCharacters(String character){

        final String initials = "ㄱgㄲkkㄴnㄷdㄸddㄹrㅁmㅂbㅃbbㅅsㅆssㅇㅈjㅉjjㅊcㅋkㅌtㅍpㅎh";// list of lead characters with romanization
        final String vowels = "ᅡaᅢaeᅣyaᅤyaeᅥeoᅦeᅧyeoᅨyeᅩoᅪwaᅫwaeᅬoeᅭyoᅮuᅯweoᅰweᅱwiᅲyuᅳeuᅴuiᅵi";// list of vowels with romanization
        final String finals = "ᆨkᆩkᆪkᆫnᆬnᆭnᆮtᆯlᆰlᆱlᆲlᆳlᆴlᆵlᆶlᆷmᆸpᆹpᆺtᆻtᆼngᆽtᆾtᆿkᇀtᇁpᇂh";// list of tail characters with romanization

        String romanized = "";

        String lead = character.substring(0,1);
        int initialIndex = initials.indexOf(lead)+1;
        while(initials.substring(initialIndex,initialIndex+1).matches("^[a-zA-Z]*$"))
        {
            romanized += initials.substring(initialIndex,initialIndex+1);
            if(initialIndex < initials.length()-1) initialIndex++;
            else break;
        }

        if(character.length() > 1){
            String vowel = character.substring(1,2);
            int vowelIndex = vowels.indexOf(vowel)+1;
            while(vowels.substring(vowelIndex,vowelIndex+1).matches("^[a-zA-Z]*$"))
            {
                romanized += vowels.substring(vowelIndex,vowelIndex+1);
                if(vowelIndex < vowels.length()-1) vowelIndex++;
                else break;
            }
        }

        if(character.length() > 2) {
            String tail = character.substring(2,3);
            int finalIndex = finals.indexOf(tail)+1;
            while (finals.substring(finalIndex, finalIndex + 1).matches("^[a-zA-Z]*$"))
            {
                romanized += finals.substring(finalIndex, finalIndex + 1);
                if(finalIndex < finals.length()-1) finalIndex++;
                else break;
            }
        }

        return romanized;
    }

    @Test
    public void transform() {

        // Girls' Generation - Into the New World (Chorus)
        String koreanText = "사랑해 널 이 느낌 이대로 " +
                "그려왔던 헤매임의 끝 " +
                "이 세상 속에서 반복되는 " +
                "슬픔 이젠 안녕 " +
                "수많은 알 수 없는 길 속에 " +
                "희미한 빛을 난 쫓아가 " +
                "언제까지라도 함께 하는 거야 "+
                "다시 만난 우리의";

        StringBuilder romanization = new StringBuilder("\n");

        for (int i = 0; i < koreanText.length(); i++) {
            final String character = koreanText.substring(i, i + 1);
            if(!character.isBlank()){
                final String decomposedCharacters = getCharacter(character);
                // System.out.println(character + " : " + decomposedCharacters);
                romanization.append(romanizeKoreanCharacters(decomposedCharacters));
            }else{
                romanization.append(" ");
            }
        }

        System.out.println(romanization.toString());
    }
}
