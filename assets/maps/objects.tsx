<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.0" name="objects" tilewidth="80" tileheight="112" tilecount="4" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="1" type="Object">
  <properties>
   <property name="animation" value="IDLE"/>
   <property name="speed" type="float" value="2"/>
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
 <tile id="3" type="Prop">
  <image source="objects/oak_tree.png" width="64" height="80"/>
  <objectgroup draworder="index" id="2">
   <object id="2" x="24" y="63">
    <polygon points="0,0 6,1 11,1 16,-1 16,-2 13,-6 13,-12 3,-12 3,-6 2,-5 1,-3"/>
   </object>
  </objectgroup>
 </tile>
 <tile id="4" type="Prop">
  <image source="objects/chest.png" width="16" height="16"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="0" y="4" width="16" height="10"/>
  </objectgroup>
 </tile>
</tileset>
