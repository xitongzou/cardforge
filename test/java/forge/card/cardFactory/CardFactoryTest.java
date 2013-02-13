package forge.card.cardFactory;

import forge.Card;
import forge.CardList;
import forge.properties.NewConstants;
import net.slightlymagic.braids.util.ClumsyRunnable;
import net.slightlymagic.braids.util.testng.BraidsAssertFunctions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.TreeSet;

//import net.slightlymagic.braids.testng.BraidsAssertFunctions;

/**
 * <p>Mana_PartTest class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
@Test(timeOut = 1000, enabled = false)
public class CardFactoryTest implements NewConstants {

	static CardFactory f;
	static {
		//Gui_NewGame.loadDynamicGamedata();
		//f = new CardFactory(ForgeProps.getFile(CARDSFOLDER));
	}
	

	/**
     * Just a quick test to see if Arc-Slogger is in the database, and if it
     * has the correct owner.
     */
    @Test(timeOut = 1000, enabled = false)
    public void test_getCard_1() {
        Card c = f.getCard("Arc-Slogger", null);
        Assert.assertNull(c.getOwner());
    }
    
    /**
     * Make sure the method throws an exception when it's supposed to.
     */
    @Test(enabled = false)
    public void test_getRandomCombinationWithoutRepetition_tooLarge() {
    	BraidsAssertFunctions.assertThrowsException(IllegalArgumentException.class,
                new ClumsyRunnable() {
                    public void run() throws Exception {
                        f.getRandomCombinationWithoutRepetition(f.size());
                    }
                });

		BraidsAssertFunctions.assertThrowsException(IllegalArgumentException.class,
                new ClumsyRunnable() {
                    public void run() throws Exception {
                        f.getRandomCombinationWithoutRepetition(f.size() / 4);
                    }
                });
    }
    
    /**
     * Make sure the method works.
     */
    @Test(enabled = false)
    public void test_getRandomCombinationWithoutRepetition_oneTenth() {
    	CardList actual = f.getRandomCombinationWithoutRepetition(f.size()/10);
    	
    	Set<String> cardNames = new TreeSet<String>();
    	
    	for (Card card: actual) {
    		Assert.assertNotNull(card);
    		cardNames.add(card.getName());
    	}
    	
    	// Make sure we got a unique set of card names and that all are
    	// accounted for.
    	Assert.assertEquals(actual.size(), cardNames.size());
    }
}
