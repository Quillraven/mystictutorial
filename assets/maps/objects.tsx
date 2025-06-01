<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.0" name="objects" tilewidth="80" tileheight="112" tilecount="5" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="1" type="Object">
  <properties>
   <property name="animation" value="IDLE"/>
   <property name="animationSpeed" type="float" value="1"/>
   <property name="life" type="int" value="12"/>
   <property name="lifeReg" type="float" value="0.25"/>
   <property name="speed" type="float" value="3.5"/>
  </properties>
  <image source="objects/player.png" width="32" height="32"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="11" y="18" width="9" height="5">
    <ellipse/>
   </object>
  </objectgroup>
 </tile>
 <tile id="2" type="Prop">
  <image source="objects/house.png" width="80" height="112"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="7" y="82" width="67" height="26"/>
  </objectgroup>
 </tile>
 <tile id="4" type="Prop">
  <image source="objects/chest.png" width="16" height="16"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="0" y="4" width="16" height="10"/>
  </objectgroup>
 </tile>
 <tile id="5" type="Prop">
  <image source="objects/oak_tree.png" width="41" height="63"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="13" y="54">
    <polygon points="0,0 6,1 11,1 16,-1 16,-2 14,-5 13,-13 3,-13 3,-6 2,-5 1,-3 0,-1"/>
   </object>
  </objectgroup>
 </tile>
 <tile id="6" type="Object">
  <properties>
   <property name="animation" value="IDLE"/>
   <property name="z" type="int" value="0"/>
  </properties>
  <image source="objects/trap.png" width="16" height="16"/>
 </tile>
</tileset>
