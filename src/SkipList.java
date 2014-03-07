import java.util.Random;

/*
 **
 ** Skip list: a skip-list node with methods to
 **     (1) generate a  random level,
 **     (2) search for  a value in the list,
 **     (3) insert a value in the list, and
 **     (4) delete a value from the list.
 **
 */

public class SkipList<EType extends Comparable<EType>> extends SkipListNode<EType>
{
    /*
     ** Constants.
     */
    private static final int MAX_LEVEL = 32;

    /*
     ** Instance variables.
     */
    private int maxLevel_;
    private int noElements_;
    private final Random rng_;
    private final SkipListNode<EType>[] searchTrail_;


    
    /*
     ** Constructor.
     */
    @SuppressWarnings("unchecked")
	public SkipList()
    {
        super(MAX_LEVEL);

        maxLevel_ = 1;
        noElements_ = 0;
        rng_ = new Random();
        searchTrail_ = SkipListNode.createArray(MAX_LEVEL, this);

        for (int i = 1; i <= MAX_LEVEL; i++)
        {
            setForwardPtr(i, null, 0);
        }
    }

    /**
     * @return a string representation of the skip list.
     */
    @Override
    public String toString()
    {
        StringBuffer sbuffer = new StringBuffer();

        SkipListNode<EType> node = this.forwardPtr(1);

        drawArrow(sbuffer, 1, maxLevel_, maxLevel_, '|', '|');
        drawArrow(sbuffer, 1, maxLevel_, maxLevel_, '|', '|');

        int nextLevels = (node == null) ? maxLevel_ : node.levels();
        drawArrow(sbuffer, 1, maxLevel_, nextLevels, '|', 'V');

        while (node != null)
        {
            sbuffer.append(node.toString(maxLevel_));
            node = node.forwardPtr(1);

            nextLevels = (node == null) ? maxLevel_ : node.levels();
            drawArrow(sbuffer, 1, maxLevel_, nextLevels, '|', 'V');
        }

        return sbuffer.toString();
    }

    /**
     * Generate a random level according to the desired probability distribution.
     *
     * @return the random level.
     */
    private int randomLevel()
    {
        int level = 1;

        while (rng_.nextFloat() < 0.5 && level < MAX_LEVEL) level++;
        return level;
    }

    /**
     * Search for a key in the skip list, keeping track of the nodes where we went "down"
     * at each level instead of moving forward. These nodes contain the pointers that may
     * need to be updated when insertions and deletions are performed.
     *
     * @param key the key to search for
     */
    private void buildSearchTrail(EType key)
    {
        SkipListNode<EType> current = this;
        SkipListNode<EType> next;
        boolean moreLevel;

        for (int level = maxLevel_; level > 0; level--)
        {
            do
            {
            	//the next one is the literally the next one at this level...
                next = current.forwardPtr(level);
                
                //then you check:
                	//a) if its not null
                	//b) if its smaller than the key 
                moreLevel = next != null && key.compareTo(next.getData()) > 0;
                if (moreLevel)
                {
                	//update
                    current = next;
                }
            }//and traverse
            while (moreLevel);
            
            //search trail contains all the head pointers in each level...
            //meaning, the base pointer
            searchTrail_[level-1] = current;
        }
    }

    /**
     * Determine whether or not the key was found by the searchTrail() method.
     *
     * @param key the key we were searching for
     * @return the node that contains this key, or null if the key is not in the skip list.
     */
    private SkipListNode<EType> found(EType key)
    {
        buildSearchTrail(key);
        SkipListNode<EType> candidateNode = searchTrail_[0].forwardPtr(1);
        return (candidateNode != null && key.equals(candidateNode.getData())) ? candidateNode : null;
    }

    /**
     *  Search for a key in a skip list.
     *
     * @param key the key we were searching for
     * @return the key we were searching for, or null if it is not in the skip list.
     */
    public EType searchKey(EType key)
    {
        SkipListNode<EType> candidateNode = found(key);
        return (candidateNode != null) ? candidateNode.getData() : null;
    }

    /**
     * Delete a key from the skip list, if it is present.
     *
     * @param key the key to delete from the skip list.
     */
    public void deleteKey(EType key)
    {
        SkipListNode<EType> nodeToDelete = found(key);

        if (nodeToDelete != null)
        {

            /*
             ** Fixup the lists at level 1 ... levelOfDeletedNode.
             */
            for (int level = nodeToDelete.levels(); level >= 1; level--)
            {
                SkipListNode<EType> start = searchTrail_[level-1];
                SkipListNode<EType> end = nodeToDelete.forwardPtr(level);

                if (start == this && end == null)
                {
                    maxLevel_--;
                }
                start.setForwardPtr(level, end);
            }

            /*
             ** Update the number of elements.
             */
            noElements_--;
        }
    }

    /**
     * Insert a new key in the skip list, if it is not already there.
     *
     * @param key the key to insert in the skip list.
     */
    public void insertKey(EType key)
    {
        if (found(key) == null)
        {

            /*
             ** Make a new list element.
             */
            noElements_++;
            SkipListNode<EType> newNode = new SkipListNode<EType>(randomLevel(), key);

            /*
             ** Fixup the lists at all levels.
             */
            //if its a new level high then you want to assign it to the 'head' node of this skip list
            for (int level = newNode.levels(); level > maxLevel_; level--)
            {
                setForwardPtr(level, newNode);
                newNode.setForwardPtr(level, null);
            }

            // if the nodes level is less than the existing maxLevel, iterate over the higher levels
            // and increment the rank of each pointer that "bypasses" the new node
            for (int level = maxLevel_; level > newNode.levels(); level--) {
            	searchTrail_[level-1].incrementForwardPtr();
            }
            //which ever is SMALLER, the max level or the or the number of levels in the new node...
            
            for (int level = Math.min(maxLevel_, newNode.levels()); level >= 1; level--)
            {
            	//first you set the forward pointer of the new node 
            	//uses the previous 'head' on each level, sets the new node's forward pointer to 
            	//point to the future obj
                newNode.setForwardPtr(level, searchTrail_[level-1].forwardPtr(level));
                
              
                searchTrail_[level-1].setForwardPtr(level, newNode);
            }
            maxLevel_ = Math.max(maxLevel_, newNode.levels());
        }
    }
}
