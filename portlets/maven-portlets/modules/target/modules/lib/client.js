
/* Module Client Library
 * (c) 2007 University of Guelph
 *---------------------------------------*/

/*-----------------------------------------------------------
 * WebProxy
 *----------------------------------------------------------- 
 * WebProxy is an extension of Ajax.Request. Usage is similar
 * to the base class, except some parameters are interpreted
 * differently. First, the request is not direct, all requests
 * go through a proxy. The first argument to the constructor
 * ('url') is passed as a parameter to the proxy and will be
 * URL encoded. Any parameters specified in the 'options' 
 * object will be appended to the request URL. This class 
 * extends the JSON support in Ajax.Request. In addition to 
 * the X-JSON HTTP header, JSON objects my be returned in the 
 * response body with Content-type: application/json. Either 
 * way, the evaluated object will be passed as the second 
 * parameter of onComplete. This is useful for handling 
 * objects that are too large for an HTTP header, such as 
 * RSS/Atom feeds.
 *-----------------------------------------------------------*/
var WebProxy = {};
WebProxy.Request = Class.create();
Object.extend(Object.extend(WebProxy.Request.prototype, Ajax.Request.prototype), {
  baseUri: '/portal/webProxy',
  initialize: function(url, options) {
    this.transport = Ajax.getTransport();
    this.setOptions(Object.extend({method:'get'},options||{}));
    this.setProxyParams(url,options);
    this.request(this.baseUri);
  },
  setProxyParams: function(url, options) {
    /* Encode user parameters as part of the request URL now,
     * and set the resulting URL+parameters as the 'url' 
     * parameter of the proxy request. */
    var parameters = this.options.parameters || '';
    if (this.options.method == 'get' && parameters.length > 0)
      url = url + (url.match(/\?/) ? '&' : '?') + parameters;
    /* If the user has requested a secure transaction, 
     * add parameters to the proxy URL for authentication. */
    var args = 'u=' + escape(url);
    if (this.options.secure) {
      if (this.options.user) { args += '&uv=' + escape(this.options.user); }
      if (this.options.pass) { args += '&pv=' + escape(this.options.pass); }
      if (this.options.userparam) { 
        args += '&up=' + escape(this.options.userparam); }
      if (this.options.passparam) { 
        args += '&pp=' + escape(this.options.passparam); }
    }
    this.options.parameters = args;
  },
  evalJSON: function() {
    try {
      if ((this.header('Content-type') || '').match(/^application\/json/i))
        return eval(this.transport.responseText);
      else
        return eval(this.header('X-JSON'));
    } catch(e) {}
  }
});

WebProxy.Updater = Class.create();
Object.extend(Object.extend(WebProxy.Updater.prototype,
                            Object.extend(WebProxy.Request.prototype,
                                          Ajax.Updater.prototype)), {
 initialize: function(container, url, options) {
   this.containers = {
     success: container.success ? $(container.success) : $(container),
     failure: container.failure ? $(container.failure) :
       (container.success ? null : $(container))
   };
   this.transport = Ajax.getTransport();
   this.setOptions(Object.extend({method:'get'},options||{}));
   this.setProxyParams(url, options);
   var onComplete = this.options.onComplete || Prototype.emptyFunction;
   this.options.onComplete = (function(transport,object) {
     this.updateContent();
     onComplete(transport, object);
   }).bind(this);
   this.request(this.baseUri);
 }
});

   
/*-----------------------------------------------------------
 * FeedProxy
 *-----------------------------------------------------------
 * FeedProxy is an extension of WebProxy. The feed proxy 
 * expects the resource identified by 'url' to be a news 
 * feed (RSS or Atom). Feeds can be converted to any supported 
 * type by setting the 'output' option. The default format is 
 * RSS 2.0. In addition to XML formats, feeds can be returned 
 * as a JSON object. Feeds are cached by the proxy to reduce 
 * network traffic.
 *-----------------------------------------------------------*/
var FeedProxy = {};
FeedProxy.Request = Class.create();
Object.extend(Object.extend(FeedProxy.Request.prototype, 
                              WebProxy.Request.prototype), {
  baseUri: '/modules/rssProxy',
  initialize: function(url, options) {
    this.transport = Ajax.getTransport();
    this.setOptions(Object.extend({method:'get'},options||{}));
    this.setProxyParams(url,options);
    this.request(this.baseUri);
  },
  setProxyParams: function(url, options) {
    /* Encode user parameters as part of the request URL now,
     * and set the resulting URL+parameters as the 'url' 
     * parameter of the proxy request. */
    var parameters = this.options.parameters || '';
    if (this.options.method == 'get' && parameters.length > 0)
      url = url + (url.match(/\?/) ? '&' : '?') + parameters;
    var args = 'u=' + escape(url);
    if (this.options.output) {
      args += '&o=' + escape(this.options.output);
    }
    this.options.parameters = args;
  }
});

FeedProxy.Updater = Class.create();
Object.extend(Object.extend(FeedProxy.Updater.prototype,
                              FeedProxy.Request.prototype), {
  initialize: function(container, url, options) {
    this.containers = {
      success: container.success ? $(container.success) : $(container),
      failure: container.failure ? $(container.failure) :
        (container.success ? null : $(container))
    };
    this.transport = Ajax.getTransport();
    this.setOptions(Object.extend({method:'get',output:'json'},options||{}));
    this.setProxyParams(url, options);
    var onComplete = this.options.onComplete || Prototype.emptyFunction;
    this.options.onComplete = (function(transport,object) {
      this.updateContent(transport, object);
      onComplete(transport, object);
    }).bind(this);
    this.request(this.baseUri);
  },

  updateContent: function(transport, object) {
    var receiver = this.responseIsSuccess() ?
      this.containers.success : this.containers.failure;
    if (receiver) {
      var html = '';
      var entries = object.entries;
      html += '<ul>';
      if (entries.length == 0) {
        html += '<li>no messages</li>';
      }
      else {
        for (var i=0; i<entries.length && i<this.options.count; i++) {
          html += '<li>';
          if (entries[i].link) {
            html += "<a target='_portal' href='"+entries[i].link+"'>";
          }
          if (entries[i].title) { html += entries[i].title; }
          if (entries[i].link) { html += "</a>"; }
          html += '</li>';
        }
      }
      html += '</ul>';
      Element.update(receiver, html);
    }
    
    if (this.responseIsSuccess()) {
      if (this.onComplete) {
        setTimeout(this.onComplete.bind(this), 10);
      }
    }
  }
});



DOM = new Object();
DOM.Visitor = {
  accept: function(node, visitor) {
    switch(node.nodeType) {
      // Element
      case ELEMENT_NODE:
        if (visitor.visitElement(node)) {
          if (node.hasChildNodes()) {
            for(var i = 0; i < node.childNodes.length; i++) {
              this.accept(node.childNodes.item(i), visitor);
            }
          }
        }
        break;
      // Text
      case TEXT_NODE:
        visitor.visitText(node);
        break;
    }
  },
  visitElement: function(node) { return true; },
  visitText: function(node) { return true; }
}


Rss = new Object();
Rss.Visitor = Class.create();
Object.extend(Rss.Visitor.prototype, DOM.Visitor);
Object.extend(Rss.Visitor.prototype, {
  visitElement: function(node) {
    if (node.nodeName == "rss") return this.visitRss(node);
    if (node.nodeName == "channel") return this.visitChannel(node);
    if (node.nodeName == "item") return this.visitItem(node);
    if (node.nodeName == "title") return this.visitTitle(node);
    if (node.nodeName == "link") return this.visitLink(node);
  },
  visitRss: function(node) { return true; },
  visitChannel: function(node) { return true; },
  visitItem: function(node) { return true; },
  visitTitle: function(node) { return true; },
  visitLink: function(node) { return true; }
});


Rss.Format = Class.create();
Object.extend(Rss.Format.prototype, Rss.Visitor.prototype);
Object.extend(Rss.Format.prototype, {
  apply: function(container, dom) {
    this.container = $(container);
    DOM.Visitor.accept(feedDOM.documentElement, this);
    this.container.appendChild(this.title);
    this.container.appendChild(this.list);
  },
  visitChannel: function(node) {
    this.title = document.createElement('A');
    this.list = document.createElement('UL');
    return true;
  },
  visitItem: function(node) {
    var item = document.createElement('LI');
    this.title = document.createElement('A');
    item.appendChild(this.title);
    this.list.appendChild(item);
    return true;
  },
  visitTitle: function(node) {
    this.title.appendChild(document.createTextNode(node.textContent));
  },
  visitLink: function(node) {
    this.title.setAttribute('HREF', node.textContent);
  }
});

