package org.zb.plugin.html2md;

/**
 * @author : ZhouBin
 */
public class MdLine {
    private int level;
    private MDLineType type;
    private final StringBuilder content;

    public MdLine(MDLineType type, int level, String content) {
        this.type = type;
        this.level = level;
        this.content = new StringBuilder(content);
    }

    public MdLine create(String line) {
        int spaces = 0;
        while ((spaces < line.length()) && (line.charAt(spaces) == ' ')) {
            spaces++;
        }
        String content = line.substring(spaces);

        int newLevel = spaces / 4;

        if (content.length() > 0) {
            if (content.matches("^[0-9]+\\.\\s.*")) {
                int c = 0;
                while ((c < content.length()) && (Character.isDigit(content.charAt(c)))) {
                    c++;
                }
                return new MdLine(MDLineType.Ordered, newLevel, content.substring(c + 2));
            } else if (content.matches("^([*+\\-])\\s.*")) {
                return new MdLine(MDLineType.Unordered, newLevel, content.substring(2));
            } else if (content.matches("^[#]+.*")) {
                int c = 0;
                while ((c < content.length()) && (content.charAt(c) == '#')) {
                    c++;
                }
                MDLineType headerType;
                switch (c) {
                    case 1:
                        headerType = MDLineType.Head1;
                        break;
                    case 2:
                        headerType = MDLineType.Head2;
                        break;
                    default:
                        headerType = MDLineType.Head3;
                        break;
                }

                while ((c < content.length()) && (content.charAt(c) == ' ')) {
                    c++;
                }

                return new MdLine(headerType, newLevel, content.substring(c));
            }
        }

        content = line.substring(4 * newLevel);

        return new MdLine(MDLineType.None, newLevel, content);
    }

    public MDLineType getListTypeName() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int i) {
        level = Math.max(i, 0);
    }

    @Override
    public String toString() {
        StringBuilder newLine = new StringBuilder();
        for (int j = 0; j < getLevel(); j++) {
            newLine.append("    ");
        }

        if (type.equals(MDLineType.Ordered)) {
            newLine.append(1).append(". ");
        } else if (type.equals(MDLineType.Unordered)) {
            newLine.append("* ");
        } else if (type.equals(MDLineType.Head1)) {
            newLine.append("# ");
        } else if (type.equals(MDLineType.Head2)) {
            newLine.append("## ");
        } else if (type.equals(MDLineType.Head3)) {
            newLine.append("### ");
        } else if (type.equals(MDLineType.HR)) {
            newLine.append("----");
        } else if (type.equals(MDLineType.BlockQuote_1)) {
            newLine.append("> ");
        } else if (type.equals(MDLineType.BlockQuote_2)) {
            newLine.append(">> ");
        }

        newLine.append(getContent());

        return newLine.toString();
    }

    public String getContent() {
        return content.toString();
    }

    public void append(String appendContent) {
        if (content.length() == 0) {
            int i = 0;
            while (i < appendContent.length() && Character.isWhitespace(appendContent.charAt(i))) {
                i++;
            }
            content.append(appendContent.substring(i));
        } else {
            content.append(appendContent);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MdLine && ((MdLine) o).type.equals(this.type);
    }

    public boolean isList() {
        return (type.equals(MDLineType.Ordered) || type.equals(MDLineType.Unordered));
    }

    public void setListType(MDLineType type2) {
        type = type2;
    }

    public enum MDLineType {
        /**
         *
         */
        Ordered, Unordered, None, Head1, Head2, Head3, HR, TR, BlockQuote_1, BlockQuote_2
    }
}