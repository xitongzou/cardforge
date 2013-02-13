
package forge;

import java.util.HashMap;

//handles "until next upkeep", "until your next upkeep" and "at beginning of upkeep" commands from cards
/**
 * <p>Upkeep class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class Upkeep implements java.io.Serializable {
	private static final long serialVersionUID = 6906459482978819354L;
	
	private HashMap<Player,CommandList> until = new HashMap<Player,CommandList>();

    /**
     * <p>addUntil.</p>
     *
     * @param p a {@link forge.Player} object
     * @param c a {@link forge.Command} object.
     */
    public void addUntil(Player p, Command c) {
    	if(null == p) p = AllZone.getPhase().getPlayerTurn();
    	
    	if(until.containsKey(p)) until.get(p).add(c);
    	else until.put(p, new CommandList(c));
    }

    /**
     * <p>executeUntil.</p>
     */
    public void executeUntil(Player p) {
    	if(until.containsKey(p)) execute(until.get(p));
    }

    /**
     * <p>sizeUntil.</p>
     *
     * @return a int.
     */
    public int sizeUntil() {
        return until.size();
    }
    
    
    private void execute(CommandList c) {
        int length = c.size();

        for (int i = 0; i < length; i++)
            c.remove(0).execute();
    }
}
