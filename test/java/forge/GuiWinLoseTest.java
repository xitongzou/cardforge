package forge;

import forge.deck.Deck;
import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA.
 * User: dhudson
 */
@Test(timeOut = 1000, enabled = false)
public class GuiWinLoseTest {
    /**
     *
     *
     */
    @Test(timeOut = 1000, enabled = false)
    public void GuiWinLoseTest1() {
        Constant.Runtime.GameType[0] = Constant.GameType.Sealed;

        Constant.Runtime.matchState.addWin();
        Constant.Runtime.matchState.addLose();

        //setup limited deck
        Deck deck = new Deck(Constant.GameType.Sealed);
        CardList pack = new CardList();
        BoosterGenerator bg = new BoosterGenerator();
        for (int i = 0; i < 6; i++)
            pack.addAll(bg.getBoosterPack()); //CardList(BoosterPack.getBoosterPack(1).toArray());


        for (int i = 0; i < pack.size(); i++) {
            if ((i % 2) == 0)
                deck.addSideboard(pack.get(i).getName());
            else
                deck.addMain(pack.get(i).getName());
        }
        Constant.Runtime.HumanDeck[0] = deck;
        //end - setup limited deck

        new Gui_WinLose();
    }
}
