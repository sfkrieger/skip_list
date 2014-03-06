/*
 **
 ** Basic skip-list node: a level and an array of references to objects.
 **
 */
public class SkipListNode<ElementType extends Comparable<ElementType>>
{
    public static final int NODE_WIDTH = 9;

    /*
     ** Private instance variables.
     */
    private int   level_;
    private ElementType data_;
    private SkipListNode<ElementType>[] forward_;

    /*
     ** Really ugly function to create an array of a (generic) type. Please
     ** ignore it and DO NOT MODIFY IT.
     */
//    @SafeVarargs
    protected static <E extends Comparable<E>> SkipListNode<E>[] createArray(int length, SkipListNode<E>... array)
    {
        return java.util.Arrays.copyOf(array, length);
    }

    /**
     * Constructor for a node with l levels and no data.
     *
     * @param l the number of levels of the new node
     */
    public SkipListNode(int l)
    {
        this(l, null);
    }

    /**
     * Constructor for a node with l levels and some data.
     *
     * @param l the number of levels of the new node
     * @param data the data contained in the node.
     */
    public SkipListNode(int l, ElementType key)
    {
        level_ = l;
        data_ = key;
        forward_ = createArray(l, this);
    }

    /**
     * @return the number of levels of the node
     */
    public int levels()
    {
        return level_;
    }

    /**
     * @return the data stored in the node.
     */
    public ElementType getData()
    {
        return data_;
    }

    /**
     * Retrieves the next node at level i after the current node.
     *
     * @param i the level we are looking at.
     * @return the next node after the current one at level i.
     */
    public SkipListNode<ElementType> forwardPtr(int i)
    {
        return forward_[i-1];
    }

    /**
     * Sets the next node at level i after the current node.
     *
     * @param i the level we are looking at.
     * @param node the new "next node" at level i.
     */
    public void setForwardPtr(int i, SkipListNode<ElementType> node)
    {
        forward_[i-1] = node;
    }

    /*
     * Helper for the toString() method.
     */
    private void oneline(StringBuffer sbuffer, int len, char start, char middle, String end)
    {
        sbuffer.append(start);
        for (int i = 0; i < len - 2; i++)
        {
            sbuffer.append(middle);
        }
        sbuffer.append(end);
    }

    /*
     * Helper for the toString() method.
     */
    protected void drawArrow(StringBuffer sbuffer, int firstLevel, int lastLevel, int nextLevels, char middle, char end)
    {
        for (int lev = firstLevel; lev <= lastLevel; lev++)
        {
            for (int j = 0; j < (NODE_WIDTH-1)/2; j++)
            {
                sbuffer.append(' ');
            }
            sbuffer.append(lev <= nextLevels ? end : middle);

            for (int j = 0; j < NODE_WIDTH/2; j++)
            {
                sbuffer.append(' ');
            }
        }
        sbuffer.append('\n');
    }

    /*
     * Helper for the toString() method.
     */
    protected String toString(int maxLevel)
    {
        StringBuffer sbuffer = new StringBuffer();

        /*
         * Top row followed by the arrows that bypass this node.
         */
        int len = level_ * NODE_WIDTH;
        oneline(sbuffer, len, '+', '-', "+");
        if (maxLevel > 0)
        {
            drawArrow(sbuffer, level_ + 1, maxLevel, 0, '|', '|');
        }

        /*
         * 2nd row followed by the arrows that bypass this node.
         */
        oneline(sbuffer, len, '|', ' ', "|");
        if (maxLevel > 0)
        {
            drawArrow(sbuffer, level_ + 1, maxLevel, 0, '|', '|');
        }

        /*
         * Row with the data followed by the arrows that bypass this node.
         * We center the data item within the node.
         */
        String label = data_.toString();
        int left = (len - label.length())/2;
        oneline(sbuffer, left, '|', ' ', " ");
        sbuffer.append(label);
        oneline(sbuffer, len - left - label.length(), ' ', ' ', "|");
        if (maxLevel > 0)
        {
            drawArrow(sbuffer, level_ + 1, maxLevel, 0, '|', '|');
        }

        /*
         * 4th row followed by the arrows that bypass this node.
         */
        oneline(sbuffer, len, '|', ' ', "|");
        if (maxLevel > 0)
        {
            drawArrow(sbuffer, level_ + 1, maxLevel, 0, '|', '|');
        }

        /*
         * Bottom row followed by the arrows that bypass this node.
         */
        oneline(sbuffer, len, '+', '-', "+");
        if (maxLevel > 0)
        {
            drawArrow(sbuffer, level_ + 1, maxLevel, 0, '|', '|');
        }

        /*
         * Arrows that come after this node (first two rows only).
         */
        drawArrow(sbuffer, 1, maxLevel, maxLevel, '|', '|');
        drawArrow(sbuffer, 1, maxLevel, maxLevel, '|', '|');
        return sbuffer.toString();
    }

    /**
     * @return a string describing this node.
     */
    @Override
    public String toString()
    {
        return toString(0);
    }
}
