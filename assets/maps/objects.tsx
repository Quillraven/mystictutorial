<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.0" name="objects" tilewidth="64" tileheight="64" tilecount="1" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="0">
  <properties>
   <property name="animation" value="IDLE"/>
   <property name="atlasAsset" value="OBJECTS"/>
   <property name="speed" type="float" value="2"/>
  </properties>
  <image source="objects/hero.png" width="64" height="64"/>
  <objectgroup draworder="index" id="3">
   <object id="2" name="collission" x="19.75" y="53.5" width="24.25" height="10.5">
    <ellipse/>
   </object>
   <object id="3" name="interact" x="0" y="8" width="64" height="64">
    <properties>
     <property name="sensor" type="bool" value="true"/>
    </properties>
    <ellipse/>
   </object>
  </objectgroup>
 </tile>
</tileset>
