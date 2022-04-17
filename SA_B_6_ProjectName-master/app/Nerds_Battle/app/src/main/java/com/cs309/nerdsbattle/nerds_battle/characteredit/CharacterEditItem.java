package com.cs309.nerdsbattle.nerds_battle.characteredit;

import com.cs309.nerdsbattle.nerds_battle.shop.Item;

/**
 * Used for tracking whether an item is equipped
 */
public class CharacterEditItem {

    private Item item;
    private int state;

    /**
     * Constructor that make an item that belongs to CharacterEditActivity
     * @param item  an Item
     * @param state state of the item. 0 = unequip, 1 = equip
     */
    public CharacterEditItem(Item item, int state) {
        this.item = item;
        this.state = state;
    }

    /**
     * Returns the item
     * @return  Item
     */
    public Item getCharacterEditItem(){
        return item;
    }

    /**
     * Set the item
     * @param item  Item
     */
    public void setCharacterEditItem(Item item){
        this.item = item;
    }

    /**
     * Check whether the item is equipped
     * @return  equip state. 0 = unequip, 1 = equip
     */
    public int getCharEditItemState(){
        return state;
    }

    /**
     * Change the item from unequip to equip or vice versa.
     * @param state equip state. 0 = unequip, 1 = equip
     */
    public void setCharEditItemState(int state){
        this.state = state;
    }
}
