function Counter() {
  var gSobject = gs.init('Counter');
  gSobject.clazz = { name: 'Counter', simpleName: 'Counter'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  Object.defineProperty(gSobject, 'style', { get: function() { return Counter.style; }, set: function(gSval) { Counter.style = gSval; }, enumerable: true });
  Object.defineProperty(gSobject, 'renderAfter', { get: function() { return Counter.renderAfter; }, set: function(gSval) { Counter.renderAfter = gSval; }, enumerable: true });
  gSobject.value = 0;
  gSobject.shadowRoot = null;
  gSobject.cId = null;
  gSobject['inc'] = function(it) {
    gSobject.value++;
    return gs.mc(gSobject,"render",[]);
  }
  gSobject['dec'] = function(it) {
    gSobject.value--;
    return gs.mc(gSobject,"render",[]);
  }
  gSobject['render'] = function(it) {
    return gs.sp(gs.gp(gs.thisOrObject(this,gSobject),"shadowRoot"),"innerHTML",(gs.plus("<style>" + (Counter.style) + "</style>", gs.execStatic(HtmlBuilder,'build', this,[function(it) {
      gs.mc(this,"h1",[gs.mc(gSobject.value,"toString",[])], gSobject);
      return gs.mc(this,"p",[function(it) {
        gs.mc(this,"button",[gs.map().add("onclick","GrooscriptGrails.recover(" + (gSobject.cId) + ").dec(this)"), "-"], gSobject);
        return gs.mc(this,"button",[gs.map().add("onclick","GrooscriptGrails.recover(" + (gSobject.cId) + ").inc(this)"), "+"], gSobject);
      }], gSobject);
    }]))));
  }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
};
Counter.style = "\n        button {\n            background-color: red;\n        }\n    ";
Counter.renderAfter = gs.list(["inc" , "dec"]);
