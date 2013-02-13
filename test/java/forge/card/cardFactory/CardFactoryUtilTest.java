package forge.card.cardFactory;

import forge.AllZone;
import forge.CardList;
import forge.CardListUtil;
import forge.Counters;
import forge.properties.NewConstants;
import org.testng.annotations.Test;

import static forge.card.cardFactory.CardFactoryUtil.*;

/**
 * <p>Mana_PartTest class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
@Test(timeOut = 1000, enabled = false)
public class CardFactoryUtilTest implements NewConstants {

    /**
     *
     */
    @Test(timeOut = 1000, enabled = false)
    public void CardFactoryTest1() {

        CardList in = AllZone.getCardFactory().getAllCards();

        CardList list = new CardList();
        list.addAll(CardListUtil.getColor(in, "black"));
        list = list.getType("Creature");

        System.out.println("Most prominent creature type: " + getMostProminentCreatureType(list));


        String manacost = "3 GW W W R B S";
        String multipliedTwice = multiplyManaCost(manacost, 2);
        String multipliedThrice = multiplyManaCost(manacost, 3);

        System.out.println(manacost + " times 2 = " + multipliedTwice);
        System.out.println(manacost + " times 3 = " + multipliedThrice);

        if (isNegativeCounter(Counters.M1M1)) {
            System.out.println("M1M1 is a bad counter!");
        } else
            System.out.println("M1M1 is a good counter!");
    }
}
