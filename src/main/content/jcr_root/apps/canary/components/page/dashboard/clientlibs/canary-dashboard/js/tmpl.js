Ember.TEMPLATES["_chart"] = Ember.Handlebars.template(function anonymous(Handlebars,depth0,helpers,partials,data) {
this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Ember.Handlebars.helpers); data = data || {};



  data.buffer.push("<h2>Chart</h2> <canvas class=\"chart\" width=\"100\" height=\"70\"></canvas>");

});

Ember.TEMPLATES["_monitorCard"] = Ember.Handlebars.template(function anonymous(Handlebars,depth0,helpers,partials,data) {
this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Ember.Handlebars.helpers); data = data || {};
  var buffer = '', stack1, helper, options, escapeExpression=this.escapeExpression, helperMissing=helpers.helperMissing, self=this;

function program1(depth0,data) {

  var buffer = '', stack1;
  data.buffer.push(" <h3 class=\"panel-title\">");
  stack1 = helpers._triageMustache.call(depth0, "name", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</h3> ");
  return buffer;
  }

function program3(depth0,data) {

  var buffer = '', stack1;
  data.buffer.push(" <div class=\"panel-body\" ");
  data.buffer.push(escapeExpression(helpers.action.call(depth0, "viewRecord", "id", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0,depth0],types:["STRING","ID"],data:data})));
  data.buffer.push("> ");
  stack1 = helpers['if'].call(depth0, "isError", {hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(4, program4, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" ");
  stack1 = helpers['if'].call(depth0, "hasRecords", {hash:{},hashTypes:{},hashContexts:{},inverse:self.program(8, program8, data),fn:self.program(6, program6, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" <p>");
  stack1 = helpers._triageMustache.call(depth0, "description", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</p> </div> <div class=\"panel-footer\" ");
  data.buffer.push(escapeExpression(helpers.action.call(depth0, "viewRecord", "id", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0,depth0],types:["STRING","ID"],data:data})));
  data.buffer.push("> <table class=\"table table-condensed table-responsive\"> ");
  stack1 = helpers['if'].call(depth0, "hasRecords", {hash:{},hashTypes:{},hashContexts:{},inverse:self.program(12, program12, data),fn:self.program(10, program10, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" <tr> <td colspan=\"2\" class=\"text-right text-muted\">Poll Interval:</td> <td colspan=\"3\">");
  stack1 = helpers._triageMustache.call(depth0, "pollInterval", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" ");
  stack1 = helpers._triageMustache.call(depth0, "pollIntervalUnit", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td class=\"text-right text-muted\">Criteria:</td> <td colspan=\"2\">");
  stack1 = helpers._triageMustache.call(depth0, "alarmThreshold", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" ");
  stack1 = helpers._triageMustache.call(depth0, "alarmCriteria", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> </tr> </table> </div> ");
  return buffer;
  }
function program4(depth0,data) {

  var buffer = '';
  data.buffer.push(" <div class=\"alert alert-danger\"> <p class=\"text-center\">Monitor has paused polling due to errors.<br><button ");
  data.buffer.push(escapeExpression(helpers.action.call(depth0, "reset", "id", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0,depth0],types:["STRING","ID"],data:data})));
  data.buffer.push(" class=\"btn btn-default btn-sm\">reset</button></p> </div> ");
  return buffer;
  }

function program6(depth0,data) {

  var buffer = '', helper, options;
  data.buffer.push(" ");
  data.buffer.push(escapeExpression((helper = helpers.render || (depth0 && depth0.render),options={hash:{
    'hideLabels': (true)
  },hashTypes:{'hideLabels': "BOOLEAN"},hashContexts:{'hideLabels': depth0},contexts:[depth0,depth0],types:["STRING","ID"],data:data},helper ? helper.call(depth0, "chart", "monitor", options) : helperMissing.call(depth0, "render", "chart", "monitor", options))));
  data.buffer.push(" ");
  return buffer;
  }

function program8(depth0,data) {


  data.buffer.push(" <div class=\"alert alert-info\"> <p>This monitor has not generated any records yet.</p> </div> ");
  }

function program10(depth0,data) {

  var buffer = '', stack1;
  data.buffer.push(" <tr> <td class=\"text-right text-muted\">Last Logged:</td> <td colspan=\"4\">");
  stack1 = helpers._triageMustache.call(depth0, "lastLoggedTime", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td colspan=\"2\" class=\"text-right text-muted\">Status:</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "status", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> </tr> <tr> <td colspan=\"2\" class=\"text-right text-muted\">Latest Dur:</td> <td colspan=\"2\">");
  stack1 = helpers._triageMustache.call(depth0, "lastLoggedDuration", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td colspan=\"3\" class=\"text-right text-muted\">Response Type:</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "lastResponseType", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> </tr> <tr> <td class=\"text-right text-muted\">Records:</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "records.records.length", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td class=\"text-right text-muted\">Avg. Dur:</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "average", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td class=\"text-right text-muted\">Max Dur:</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "maxDuration", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td class=\"text-right text-muted\">Min Dur:</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "minDuration", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> </tr> ");
  return buffer;
  }

function program12(depth0,data) {


  data.buffer.push(" <tr> <td colspan=\"8\"> This monitor has not generated any records yet. </td> </tr> ");
  }

  data.buffer.push("<div ");
  data.buffer.push(escapeExpression(helpers['bind-attr'].call(depth0, {hash:{
    'class': (":panel isError:panel-danger isWarning:panel-warning isNormal:panel-success")
  },hashTypes:{'class': "STRING"},hashContexts:{'class': depth0},contexts:[],types:[],data:data})));
  data.buffer.push("> <div class=\"panel-heading\"> <span ");
  data.buffer.push(escapeExpression(helpers.action.call(depth0, "toggleCard", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["STRING"],data:data})));
  data.buffer.push(" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"View Monitor Details\" ");
  data.buffer.push(escapeExpression(helpers['bind-attr'].call(depth0, {hash:{
    'class': (":floated-icon :pull-right :glyphicon :has-tooltip isCardExpanded:glyphicon-minus isCardExpanded::glyphicon-plus")
  },hashTypes:{'class': "STRING"},hashContexts:{'class': depth0},contexts:[],types:[],data:data})));
  data.buffer.push("></span> ");
  stack1 = (helper = helpers['link-to'] || (depth0 && depth0['link-to']),options={hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(1, program1, data),contexts:[depth0,depth0],types:["STRING","ID"],data:data},helper ? helper.call(depth0, "monitor", "identifier", options) : helperMissing.call(depth0, "link-to", "monitor", "identifier", options));
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" </div> ");
  stack1 = helpers['if'].call(depth0, "isCardExpanded", {hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(3, program3, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" </div>");
  return buffer;

});

Ember.TEMPLATES["_monitorTableIfEmpty"] = Ember.Handlebars.template(function anonymous(Handlebars,depth0,helpers,partials,data) {
this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Ember.Handlebars.helpers); data = data || {};



  data.buffer.push(" <tr> <td colspan=\"6\"> There are no monitors to display! </td> </tr>");

});

Ember.TEMPLATES["_monitorTableRow"] = Ember.Handlebars.template(function anonymous(Handlebars,depth0,helpers,partials,data) {
this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Ember.Handlebars.helpers); data = data || {};
  var buffer = '', stack1, escapeExpression=this.escapeExpression, self=this;

function program1(depth0,data) {

  var buffer = '', stack1;
  data.buffer.push(" <td>");
  stack1 = helpers._triageMustache.call(depth0, "lastLoggedTime", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "status", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "average", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "lastResponseType", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> ");
  return buffer;
  }

function program3(depth0,data) {


  data.buffer.push(" <td colspan=\"4\"> This monitor has not generated any records yet. </td> ");
  }

function program5(depth0,data) {

  var buffer = '';
  data.buffer.push(" <button class=\"btn btn-default btn-xs\" ");
  data.buffer.push(escapeExpression(helpers.action.call(depth0, "resetAlarm", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["STRING"],data:data})));
  data.buffer.push(">reset</button> ");
  return buffer;
  }

  data.buffer.push("<tr ");
  data.buffer.push(escapeExpression(helpers.action.call(depth0, "viewRecord", "identifier", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0,depth0],types:["STRING","ID"],data:data})));
  data.buffer.push(" ");
  data.buffer.push(escapeExpression(helpers['bind-attr'].call(depth0, {hash:{
    'class': ("isError:danger isWarning:warning isNormal:success")
  },hashTypes:{'class': "STRING"},hashContexts:{'class': depth0},contexts:[],types:[],data:data})));
  data.buffer.push("> <td><!-- ");
  stack1 = helpers._triageMustache.call(depth0, "identifier", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" -->");
  stack1 = helpers._triageMustache.call(depth0, "name", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "description", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> ");
  stack1 = helpers['if'].call(depth0, "hasRecords", {hash:{},hashTypes:{},hashContexts:{},inverse:self.program(3, program3, data),fn:self.program(1, program1, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" <td> ");
  stack1 = helpers['if'].call(depth0, "isError", {hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(5, program5, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" </td> </tr> ");
  return buffer;

});

Ember.TEMPLATES["application"] = Ember.Handlebars.template(function anonymous(Handlebars,depth0,helpers,partials,data) {
this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Ember.Handlebars.helpers); data = data || {};
  var buffer = '', stack1, helper, options, self=this, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;

function program1(depth0,data) {


  data.buffer.push("Monitors");
  }

function program3(depth0,data) {


  data.buffer.push("Settings");
  }

function program5(depth0,data) {


  data.buffer.push("Help");
  }

  data.buffer.push(" <div class=\"navbar navbar-inverse navbar-fixed-top\" role=\"navigation\"> <div class=\"container-fluid\"> <div class=\"navbar-header\"> <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\".navbar-collapse\"> <span class=\"sr-only\">Toggle navigation</span> <span class=\"icon-bar\"></span> <span class=\"icon-bar\"></span> <span class=\"icon-bar\"></span> </button> <a class=\"navbar-brand\" href=\"#\">Canary CQ Service Monitor</a> </div> <div class=\"navbar-collapse collapse\"> <ul class=\"nav navbar-nav navbar-right\"> <li>");
  stack1 = (helper = helpers['link-to'] || (depth0 && depth0['link-to']),options={hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(1, program1, data),contexts:[depth0],types:["STRING"],data:data},helper ? helper.call(depth0, "monitors", options) : helperMissing.call(depth0, "link-to", "monitors", options));
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</li> <!--<li>");
  stack1 = (helper = helpers['link-to'] || (depth0 && depth0['link-to']),options={hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(3, program3, data),contexts:[depth0],types:["STRING"],data:data},helper ? helper.call(depth0, "settings", options) : helperMissing.call(depth0, "link-to", "settings", options));
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</li>--> <li>");
  stack1 = (helper = helpers['link-to'] || (depth0 && depth0['link-to']),options={hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(5, program5, data),contexts:[depth0],types:["STRING"],data:data},helper ? helper.call(depth0, "help", options) : helperMissing.call(depth0, "link-to", "help", options));
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</li> </ul> <form class=\"navbar-form navbar-right\"> ");
  data.buffer.push(escapeExpression((helper = helpers.input || (depth0 && depth0.input),options={hash:{
    'type': ("text"),
    'class': ("form-control"),
    'placeholder': ("Search Monitors.."),
    'value': ("search"),
    'action': ("query")
  },hashTypes:{'type': "STRING",'class': "STRING",'placeholder': "STRING",'value': "ID",'action': "STRING"},hashContexts:{'type': depth0,'class': depth0,'placeholder': depth0,'value': depth0,'action': depth0},contexts:[],types:[],data:data},helper ? helper.call(depth0, options) : helperMissing.call(depth0, "input", options))));
  data.buffer.push(" <button ");
  data.buffer.push(escapeExpression(helpers.action.call(depth0, "query", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["STRING"],data:data})));
  data.buffer.push(" class=\"btn btn-default\"><span class=\"glyphicon glyphicon-search\"></span></button> </form> ");
  data.buffer.push(escapeExpression((helper = helpers.outlet || (depth0 && depth0.outlet),options={hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data},helper ? helper.call(depth0, "alerts", options) : helperMissing.call(depth0, "outlet", "alerts", options))));
  data.buffer.push(" </div> </div> </div> <!-- .navbar --> <div class=\"container-fluid\"> <div class=\"row\"> <!-- <div class=\"col-sm-3 col-md-2 sidebar\"> <ul id=\"viewMenu\" class=\"nav nav-sidebar\"> <li id=\"vm_monitors\" class=\"active\">");
  stack1 = (helper = helpers['link-to'] || (depth0 && depth0['link-to']),options={hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(1, program1, data),contexts:[depth0],types:["STRING"],data:data},helper ? helper.call(depth0, "monitors", options) : helperMissing.call(depth0, "link-to", "monitors", options));
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</li> <li id=\"vm_alerts\">Alerts</li> </ul> </div> <div class=\"col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main\"> --> <div class=\"col-md-12 main\"> ");
  data.buffer.push(escapeExpression((helper = helpers.outlet || (depth0 && depth0.outlet),options={hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data},helper ? helper.call(depth0, "main", options) : helperMissing.call(depth0, "outlet", "main", options))));
  data.buffer.push(" </div> <!-- .main --> </div> <!-- .row --> </div> <!-- .container-fluid -->");
  return buffer;

});

Ember.TEMPLATES["chart"] = Ember.Handlebars.template(function anonymous(Handlebars,depth0,helpers,partials,data) {
this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Ember.Handlebars.helpers); data = data || {};



  data.buffer.push("<!-- <h2>Chart</h2> --> <canvas class=\"chart\" width=\"100\" height=\"70\"></canvas>");

});

Ember.TEMPLATES["error"] = Ember.Handlebars.template(function anonymous(Handlebars,depth0,helpers,partials,data) {
this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Ember.Handlebars.helpers); data = data || {};



  data.buffer.push("<h1>An Error Occurred While Loading Data</h1>");

});

Ember.TEMPLATES["help"] = Ember.Handlebars.template(function anonymous(Handlebars,depth0,helpers,partials,data) {
this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Ember.Handlebars.helpers); data = data || {};



  data.buffer.push("<section> <div class=\"row\"> <div class=\"col-md-12\"> <h1>CQ Canary Framework</h1> <p> The Canary framework is a simple, stand alone monitoring, profiling, and reporting tool for OSGi components and services within AEM. The framework enables scheduled, custom monitoring or profiling of critical aspects of an AEM instance. Canary provides a conduit between Monitors, which collect data, and other registered service types that can distribute and execute on that data. In addition to polling and distribution, Canary provides an API for talking to services in the framework, JMX beans for reporting and maintenance, and a rich UI for representing this data. </p> <p> The AEM Canary framework consists of registered Monitor(s) that provide responses to poll requests. These poll requests are collected and analyzed in the framework based on supplied configurations. </p> <h2>Monitors</h2> <p> Monitors are the source of data for the Canary Framework. Monitors are polled by the framework based on their specific configurations – an interval and a time unit (seconds, minutes, etc…). Monitors can poll up to once per second. Any configuration unit smaller than a single second will be rounded up to a second. If a poll exceeds the maximum execution time, it will be interrupted and a failure will be recorded indicting the interruption. Poll responses are recorded in-memory and routed to Mission Control for distribution to all Poll Response Handlers. </p> <p> In order to detect an alarm state, poll data is analyzed each time a poll is pushed into the in-memory record storage. The configured alarm threshold and criteria are used to determine and flag an alarm state. Monitors support alarm criteria: Recent Polls, Average Execution Time, or Average Failure Rate. Note: The averaged alarm criteria are affected by the max number of records configured for the monitor. </p> <h2>Documentation</h2> <p>For more information, visit the <a href=\"http://code.citytechinc.com/canary/\">Maven site</a> or the <a href=\"https://github.com/Citytechinc/canary\">Github project</a>.</p> <h2>About</h2> <p>This project is hosted on <a href=\"https://github.com/Citytechinc/canary\">Github project</a> for <a href=\"http://www.citytechinc.com/\">CITYTECH, Inc.</a></p> <p>The UI was built using <a href=\"http://emberjs.com/\">ember.js</a>, <a href=\"http://www.chartjs.org/\">chart.js</a> and <a href=\"http://www.getbootstrap.com/\">bootstrap</a>.</p> </div> </div> </section>");

});

Ember.TEMPLATES["loading"] = Ember.Handlebars.template(function anonymous(Handlebars,depth0,helpers,partials,data) {
this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Ember.Handlebars.helpers); data = data || {};



  data.buffer.push("<div class=\"col-md-12\"> <p><i class=\"loader\"></i>&nbsp;Loading <em>Canary CQ Service Monitor</em> Data&hellip;</p> </div>");

});

Ember.TEMPLATES["monitor"] = Ember.Handlebars.template(function anonymous(Handlebars,depth0,helpers,partials,data) {
this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Ember.Handlebars.helpers); data = data || {};
  var buffer = '', stack1, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, self=this;

function program1(depth0,data) {

  var buffer = '', helper, options;
  data.buffer.push(" ");
  data.buffer.push(escapeExpression((helper = helpers.render || (depth0 && depth0.render),options={hash:{
    'hideLabels': (false)
  },hashTypes:{'hideLabels': "BOOLEAN"},hashContexts:{'hideLabels': depth0},contexts:[depth0,depth0],types:["STRING","ID"],data:data},helper ? helper.call(depth0, "chart", "controller", options) : helperMissing.call(depth0, "render", "chart", "controller", options))));
  data.buffer.push(" ");
  return buffer;
  }

function program3(depth0,data) {


  data.buffer.push(" <div class=\"alert alert-info\"> <p>This monitor has not generated any records yet.</p> </div> ");
  }

function program5(depth0,data) {

  var buffer = '';
  data.buffer.push(" <div class=\"alert alert-danger\"> <p class=\"text-center\">Monitor has paused polling due to errors.<br> <a href=\"#\" ");
  data.buffer.push(escapeExpression(helpers.action.call(depth0, "resetAlarm", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["STRING"],data:data})));
  data.buffer.push(" class=\"btn btn-default btn-sm\">reset</a> </div> ");
  return buffer;
  }

function program7(depth0,data) {

  var buffer = '', stack1;
  data.buffer.push(" <dl class=\"dl-horizontal\"> <dt>Current Status</dt> <dd>");
  stack1 = helpers._triageMustache.call(depth0, "status", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</dd> <dt>Last Logged</dt> <dd>");
  stack1 = helpers._triageMustache.call(depth0, "lastLoggedTime", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</dd> <dt>Latest Duration</dt> <dd>");
  stack1 = helpers._triageMustache.call(depth0, "lastLoggedDuration", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</dd> <dt>Latest Response Type</dt> <dd>");
  stack1 = helpers._triageMustache.call(depth0, "lastResponseType", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</dd> <dt>Records Stored</dt> <dd>");
  stack1 = helpers._triageMustache.call(depth0, "records.records.length", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</dd> <dt>Average Duration</dt> <dd>");
  stack1 = helpers._triageMustache.call(depth0, "average", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</dd> <dt>Max Duration</dt> <dd>");
  stack1 = helpers._triageMustache.call(depth0, "maxDuration", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</dd> <dt>Min Duration</dt> <dd>");
  stack1 = helpers._triageMustache.call(depth0, "minDuration", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</dd> </dd> ");
  return buffer;
  }

function program9(depth0,data) {

  var buffer = '', stack1;
  data.buffer.push(" <dt>Reset Criteria</dt> <dd>");
  stack1 = helpers._triageMustache.call(depth0, "resetInterval", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" ");
  stack1 = helpers._triageMustache.call(depth0, "resetIntervalUnit", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</dd> ");
  return buffer;
  }

function program11(depth0,data) {


  data.buffer.push(" <dt>Reset Criteria</dt> <dd>Not Configured</dd> ");
  }

function program13(depth0,data) {

  var buffer = '', stack1;
  data.buffer.push(" <table class=\"table table-condensed table-hover record-data-table\"> <tbody> <tr> <th>Start Time</th> <th>End Time</th> <th>Duration (ms)</th> <th>Excused</th> <th>Response Type</th> </tr> ");
  stack1 = helpers.each.call(depth0, "records.records", {hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(14, program14, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" </tbody> </table> ");
  return buffer;
  }
function program14(depth0,data) {

  var buffer = '', stack1, helper, options;
  data.buffer.push(" <tr class=\"record-data\"> <td>");
  stack1 = helpers._triageMustache.call(depth0, "startTime", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "endTime", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td>");
  data.buffer.push(escapeExpression((helper = helpers.duration || (depth0 && depth0.duration),options={hash:{},hashTypes:{},hashContexts:{},contexts:[depth0,depth0],types:["ID","ID"],data:data},helper ? helper.call(depth0, "startTime", "endTime", options) : helperMissing.call(depth0, "duration", "startTime", "endTime", options))));
  data.buffer.push("</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "excused", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "responseType", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> </tr> ");
  stack1 = helpers['if'].call(depth0, "messages", {hash:{},hashTypes:{},hashContexts:{},inverse:self.program(18, program18, data),fn:self.program(15, program15, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" ");
  return buffer;
  }
function program15(depth0,data) {

  var buffer = '', stack1;
  data.buffer.push(" ");
  stack1 = helpers.each.call(depth0, "message", "in", "messages", {hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(16, program16, data),contexts:[depth0,depth0,depth0],types:["ID","ID","ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" ");
  return buffer;
  }
function program16(depth0,data) {

  var buffer = '', stack1;
  data.buffer.push(" <tr><td colspan=\"5\" class=\"record-message\"><strong>Message:</strong>&nbsp;");
  stack1 = helpers._triageMustache.call(depth0, "message", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td></tr> ");
  return buffer;
  }

function program18(depth0,data) {


  data.buffer.push(" <!-- no messages --> ");
  }

  data.buffer.push("<section id=\"record\"> <div class=\"row\"> <div class=\"col-md-12\"> <h1 class=\"page-header\">");
  stack1 = helpers._triageMustache.call(depth0, "name", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</h1> </div> <div class=\"col-md-12 alerts\"> <p class=\"alert alert-info\">");
  stack1 = helpers._triageMustache.call(depth0, "alert", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</p> </div> </div> <!-- /row --> <div class=\"row\"> <div class=\"col-md-8 col-md-push-4\"> ");
  stack1 = helpers['if'].call(depth0, "hasRecords", {hash:{},hashTypes:{},hashContexts:{},inverse:self.program(3, program3, data),fn:self.program(1, program1, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" </div> <div class=\"col-md-4 col-md-pull-8\"> <div class=\"panel panel-default\"> <div class=\"panel-heading\"> <h2>Monitor Status</h2> </div> <div class=\"panel-body\"> ");
  stack1 = helpers['if'].call(depth0, "isError", {hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(5, program5, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" <h3>Status</h3> ");
  stack1 = helpers['if'].call(depth0, "hasRecords", {hash:{},hashTypes:{},hashContexts:{},inverse:self.program(3, program3, data),fn:self.program(7, program7, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" <hr> <h3>Configuration</h3> <dl class=\"dl-horizontal\"> <dt>Alarm Criteria</dt> <dd>");
  stack1 = helpers._triageMustache.call(depth0, "alarmThreshold", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" ");
  stack1 = helpers._triageMustache.call(depth0, "alarmCriteria", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</dd> <dt>Alarm Condition</dt> <dd>");
  stack1 = helpers._triageMustache.call(depth0, "maxExecutionTime", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" ms</dd> <dt>Polling Interval</dt> <dd>");
  stack1 = helpers._triageMustache.call(depth0, "pollInterval", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" ");
  stack1 = helpers._triageMustache.call(depth0, "pollIntervalUnit", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</dd> <dt>Persist on Alarm?</dt> <dd>");
  stack1 = helpers._triageMustache.call(depth0, "persistWhenAlarmed", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</dd> ");
  stack1 = helpers['if'].call(depth0, "resetCriterialDefined", {hash:{},hashTypes:{},hashContexts:{},inverse:self.program(11, program11, data),fn:self.program(9, program9, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" <dt>Maximum Records</dt> <dd>");
  stack1 = helpers._triageMustache.call(depth0, "maxNumberOfRecords", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</dd> </dd> </div> </div> </div> </div> <!-- /row --> <div class=\"row\"> <div class=\"col-md-12\"> <div class=\"table-responsive\"> <h2>Records</h2> ");
  stack1 = helpers['if'].call(depth0, "hasRecords", {hash:{},hashTypes:{},hashContexts:{},inverse:self.program(3, program3, data),fn:self.program(13, program13, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" </div> </div> </div> <!-- /row --> </section>");
  return buffer;

});

Ember.TEMPLATES["monitors"] = Ember.Handlebars.template(function anonymous(Handlebars,depth0,helpers,partials,data) {
this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Ember.Handlebars.helpers); data = data || {};
  var buffer = '', stack1, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, self=this;

function program1(depth0,data) {

  var buffer = '', stack1;
  data.buffer.push(" <div id=\"monitor-cards\"> ");
  stack1 = helpers.each.call(depth0, "monitor", "in", "errors", {hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(2, program2, data),contexts:[depth0,depth0,depth0],types:["ID","ID","ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" ");
  stack1 = helpers.each.call(depth0, "monitor", "in", "warnings", {hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(2, program2, data),contexts:[depth0,depth0,depth0],types:["ID","ID","ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" ");
  stack1 = helpers.each.call(depth0, "monitor", "in", "normals", {hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(2, program2, data),contexts:[depth0,depth0,depth0],types:["ID","ID","ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" </div> ");
  return buffer;
  }
function program2(depth0,data) {

  var buffer = '', helper, options;
  data.buffer.push(" <div class=\"col-sm-6 col-md-6 col-lg-4\"> ");
  data.buffer.push(escapeExpression((helper = helpers.partial || (depth0 && depth0.partial),options={hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["STRING"],data:data},helper ? helper.call(depth0, "monitorCard", options) : helperMissing.call(depth0, "partial", "monitorCard", options))));
  data.buffer.push(" </div> ");
  return buffer;
  }

function program4(depth0,data) {

  var buffer = '', stack1;
  data.buffer.push(" ");
  stack1 = helpers['if'].call(depth0, "errorCount", {hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(5, program5, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" ");
  stack1 = helpers['if'].call(depth0, "warnCount", {hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(8, program8, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" <table class=\"table table-striped table-hover\"> <caption><h2 class=\"text-left\">Normal Services</h2class=\"text-left\"></caption> <thead><tr><th>Service Name</th><th>Description</th><th>Last Logged At</th><th>Status</th><th>Average</th><th>Response</th></tr></thead> <tfoot><tr><td colspan=\"7\" class=\"text-right\">Showing ");
  stack1 = helpers._triageMustache.call(depth0, "normalCount", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" monitors.</td></tr></tfoot> <tbody> ");
  stack1 = helpers.each.call(depth0, "normals", {hash:{},hashTypes:{},hashContexts:{},inverse:self.program(10, program10, data),fn:self.program(6, program6, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" </tbody> </table> ");
  return buffer;
  }
function program5(depth0,data) {

  var buffer = '', stack1;
  data.buffer.push(" <table class=\"table table-striped table-hover table-responsive\"> <caption><h2 class=\"text-left\">Service Errors</h2></caption> <thead><tr><th>Service Name</th><th>Description</th><th>Last Logged At</th><th>Status</th><th>Average</th><th>Response</th><th>&nbsp;</th></tr></thead> <tfoot><tr><td colspan=\"7\" class=\"text-right\">Showing ");
  stack1 = helpers._triageMustache.call(depth0, "errorCount", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" monitors.</td></tr></tfoot> <tbody> ");
  stack1 = helpers.each.call(depth0, "errors", {hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(6, program6, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" </tbody> </table> ");
  return buffer;
  }
function program6(depth0,data) {

  var buffer = '', helper, options;
  data.buffer.push(" ");
  data.buffer.push(escapeExpression((helper = helpers.partial || (depth0 && depth0.partial),options={hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["STRING"],data:data},helper ? helper.call(depth0, "monitorTableRow", options) : helperMissing.call(depth0, "partial", "monitorTableRow", options))));
  data.buffer.push(" ");
  return buffer;
  }

function program8(depth0,data) {

  var buffer = '', stack1;
  data.buffer.push(" <table class=\"table table-striped table-hover\"> <caption><h2 class=\"text-left\">Service Warnings</h2<</caption> <thead><tr><th>Service Name</th><th>Description</th><th>Last Logged At</th><th>Status</th><th>Average</th><th>Response</th></tr></thead> <tfoot><tr><td colspan=\"7\" class=\"text-right\">Showing ");
  stack1 = helpers._triageMustache.call(depth0, "warnCount", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" monitors.</td></tr></tfoot> <tbody> ");
  stack1 = helpers.each.call(depth0, "warnings", {hash:{},hashTypes:{},hashContexts:{},inverse:self.noop,fn:self.program(6, program6, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" </tbody> </table> ");
  return buffer;
  }

function program10(depth0,data) {

  var buffer = '', helper, options;
  data.buffer.push(" ");
  data.buffer.push(escapeExpression((helper = helpers.partial || (depth0 && depth0.partial),options={hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["STRING"],data:data},helper ? helper.call(depth0, "monitorTableIfEmpty", options) : helperMissing.call(depth0, "partial", "monitorTableIfEmpty", options))));
  data.buffer.push(" ");
  return buffer;
  }

  data.buffer.push("<section class=\"row\" id=\"reports\"> <div class=\"md-col-12\"> <div class=\"btn-group pull-right\"> <button type=\"button\" ");
  data.buffer.push(escapeExpression(helpers['bind-attr'].call(depth0, {hash:{
    'class': (":btn :btn-default showGridView::active")
  },hashTypes:{'class': "STRING"},hashContexts:{'class': depth0},contexts:[],types:[],data:data})));
  data.buffer.push(" ");
  data.buffer.push(escapeExpression(helpers.action.call(depth0, "showTableview", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["STRING"],data:data})));
  data.buffer.push("><i class=\"glyphicon glyphicon-th-list\"></i></button> <button type=\"button\" ");
  data.buffer.push(escapeExpression(helpers['bind-attr'].call(depth0, {hash:{
    'class': (":btn :btn-default showGridView:active")
  },hashTypes:{'class': "STRING"},hashContexts:{'class': depth0},contexts:[],types:[],data:data})));
  data.buffer.push(" ");
  data.buffer.push(escapeExpression(helpers.action.call(depth0, "showGridView", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["STRING"],data:data})));
  data.buffer.push("><i class=\"glyphicon glyphicon-th\"></i></button> </div> <h1 class=\"page-header\">Monitors</h1> ");
  stack1 = helpers['if'].call(depth0, "showGridView", {hash:{},hashTypes:{},hashContexts:{},inverse:self.program(4, program4, data),fn:self.program(1, program1, data),contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" </div> <!-- md-col-12 --> </section>");
  return buffer;

});

Ember.TEMPLATES["search"] = Ember.Handlebars.template(function anonymous(Handlebars,depth0,helpers,partials,data) {
this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Ember.Handlebars.helpers); data = data || {};
  var buffer = '', stack1, escapeExpression=this.escapeExpression, helperMissing=helpers.helperMissing, self=this;

function program1(depth0,data) {

  var buffer = '', stack1;
  data.buffer.push(" <tr ");
  data.buffer.push(escapeExpression(helpers.action.call(depth0, "viewRecord", "identifier", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0,depth0],types:["STRING","ID"],data:data})));
  data.buffer.push(" ");
  data.buffer.push(escapeExpression(helpers['bind-attr'].call(depth0, {hash:{
    'class': ("isError:danger isWarning:warning isNormal:success")
  },hashTypes:{'class': "STRING"},hashContexts:{'class': depth0},contexts:[],types:[],data:data})));
  data.buffer.push("> <td>");
  stack1 = helpers._triageMustache.call(depth0, "identifier", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "name", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "lastLoggedTime", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "status", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> <td>");
  stack1 = helpers._triageMustache.call(depth0, "lastResponseType", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("</td> </tr> ");
  return buffer;
  }

function program3(depth0,data) {

  var buffer = '', helper, options;
  data.buffer.push(" ");
  data.buffer.push(escapeExpression((helper = helpers.partial || (depth0 && depth0.partial),options={hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["STRING"],data:data},helper ? helper.call(depth0, "monitorTableIfEmpty", options) : helperMissing.call(depth0, "partial", "monitorTableIfEmpty", options))));
  data.buffer.push(" ");
  return buffer;
  }

  data.buffer.push(" <section class=\"row\" id=\"reports\"> <h1 class=\"page-header\">Search Monitors</h1> <table class=\"table table-striped table-hover\"> <caption><h2 class=\"text-left\">Search Term: '");
  stack1 = helpers._triageMustache.call(depth0, "search", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push("'</h2class=\"text-left\"></caption> <thead><tr><th>Identifier</th><th>Service Name</th><th>Last Logged At</th><th>Status</th><th>Response</th></tr></thead> <tfoot> <tr> <td colspan=\"6\" class=\"text-right\">Showing ");
  stack1 = helpers._triageMustache.call(depth0, "length", {hash:{},hashTypes:{},hashContexts:{},contexts:[depth0],types:["ID"],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" monitors.</td> </tr> </tfoot> <tbody> ");
  stack1 = helpers.each.call(depth0, {hash:{},hashTypes:{},hashContexts:{},inverse:self.program(3, program3, data),fn:self.program(1, program1, data),contexts:[],types:[],data:data});
  if(stack1 || stack1 === 0) { data.buffer.push(stack1); }
  data.buffer.push(" </tbody> </table> </section>");
  return buffer;

});

Ember.TEMPLATES["settings"] = Ember.Handlebars.template(function anonymous(Handlebars,depth0,helpers,partials,data) {
this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Ember.Handlebars.helpers); data = data || {};



  data.buffer.push("<section> <div class=\"row\"> <div class=\"col-md-12\"> <h1>Canary Settings</h1> <p>Lorem ipsum dolor sit amet.</p> </div> </div> </section>");

});