package io.gamioo.sandbox;

import java.util.List;



public class ItemTemplate   {

    /**
    * 道具ID
    * int id
    * 
    */
    private  int id;

    /**
    * 道具名称
    * language name
    * 
    */
    private  String name;

    /**
    * 是否可在背包中使用
    * int useAble
    * 
    */
    private  int useAble;

    /**
    * 道具效果类型
    * int effectType
    * 
    */
    private  int effectType;

    /**
    * 道具效果ID
    * int effectId
    * 
    */
    private  int effectId;

    /**
    * 道具效果value
    * int effectValue
    * 
    */
    private  int effectValue;

    /**
    * 道具效果value2
    * int effectValue2
    * 
    */
    private  int effectValue2;

    /**
    * 道具批量使用上限
    * int batchUseLimit
    * 
    */
    private  int batchUseLimit;

    /**
    * 道具拥有上限
    * int ownLimit
    * 
    */
    private  int ownLimit;

    /**
    * 道具冷却时间
    * int cdSeconds
    * 
    */
    private  int cdSeconds;

    /**
    * 品质
    * int quality
    * 
    */
    private  int quality;

    /**
    * 最小使用等级限制
    * int minLevel
    * 
    */
    private  int minLevel;

    /**
    * 最大使用等级限制
    * int maxLevel
    * 
    */
    private  int maxLevel;

    /**
    * 道具回收配置
    * string withdrawParam
    * 
    */
    private  String withdrawParam;

    /**
    * 道具回收时兑换变成的道具
    * pairarray withdrawItemId
    * 
    */

    private List<IntPairType> withdrawItemIdPairList;

    /**
    * 道具模型
    * string model
    * 
    */
    private  String model;

    /**
    * 是否自动使用
    * bool useNow
    * 
    */
    private  boolean useNow;

    /**
    * 道具重复使用后可兑换道具
    * pairarray exchangeItemId
    * 
    */
    private List<IntPairType> exchangeItemIdPairList;

    /**
    * 道具占战役背包多大
    * int itemCost
    * 
    */
    private  int itemCost;



    /**
    * 道具ID
    * int id
    * 
    */
    public int getId(){
        return id;
    }

    /**
    * 道具名称
    * language name
    * 
    */
    public String getName(){
        return name;
    }
    /**
    * 是否可在背包中使用
    * int useAble
    * 
    */
    public int getUseAble(){
        return useAble;
    }

    /**
    * 道具效果类型
    * int effectType
    * 
    */
    public int getEffectType(){
        return effectType;
    }

    /**
    * 道具效果ID
    * int effectId
    * 
    */
    public int getEffectId(){
        return effectId;
    }

    /**
    * 道具效果value
    * int effectValue
    * 
    */
    public int getEffectValue(){
        return effectValue;
    }

    /**
    * 道具效果value2
    * int effectValue2
    * 
    */
    public int getEffectValue2(){
        return effectValue2;
    }

    /**
    * 道具批量使用上限
    * int batchUseLimit
    * 
    */
    public int getBatchUseLimit(){
        return batchUseLimit;
    }

    /**
    * 道具拥有上限
    * int ownLimit
    * 
    */
    public int getOwnLimit(){
        return ownLimit;
    }

    /**
    * 道具冷却时间
    * int cdSeconds
    * 
    */
    public int getCdSeconds(){
        return cdSeconds;
    }

    /**
    * 品质
    * int quality
    * 
    */
    public int getQuality(){
        return quality;
    }

    /**
    * 最小使用等级限制
    * int minLevel
    * 
    */
    public int getMinLevel(){
        return minLevel;
    }

    /**
    * 最大使用等级限制
    * int maxLevel
    * 
    */
    public int getMaxLevel(){
        return maxLevel;
    }

    /**
    * 道具回收配置
    * string withdrawParam
    * 
    */
    public String getWithdrawParam(){
        return withdrawParam;
    }

    /**
    * 道具回收时兑换变成的道具
    * pairarray withdrawItemId
    * 
    */
    public List<IntPairType> getWithdrawItemIdPairList(){
        return withdrawItemIdPairList;
    }
    /**
    * 道具模型
    * string model
    * 
    */
    public String getModel(){
        return model;
    }

    /**
    * 是否自动使用
    * bool useNow
    * 
    */
    public boolean getUseNow(){
        return useNow;
    }

    /**
    * 道具重复使用后可兑换道具
    * pairarray exchangeItemId
    * 
    */
    public List<IntPairType> getExchangeItemIdPairList(){
        return exchangeItemIdPairList;
    }
    /**
    * 道具占战役背包多大
    * int itemCost
    * 
    */
    public int getItemCost(){
        return itemCost;
    }


}