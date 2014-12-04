// $Header: /usr/local/repos/portlets/modules/src/main/webapp/lib/rdf.js,v 1.2 2007/04/04 17:24:57 brharp Exp $

// In which we define functions for parsing and querying RDF.

var RDF = {
  revision: '$Revision: 1.2 $',
  
  varp: function (x) {
    return x.charAt(0) == '?';
  },
    
  subject: function (triple) {
    return triple.s;
  },

  predicate: function (triple) {
    return triple.p;
  },

  object: function (triple) {
    return triple.o;
  },

  unify: function (a, b, e) {
    if (typeof a == 'string' && typeof b == 'string') {
      if (RDF.varp(a) && RDF.varp(b)) {
        return e[a] == e[b];
      }
      else if (RDF.varp(a)) {
        if (e[a]) {
          return e[a] == b;
        } else {
          e[a] = b;
          return true;
        }
      }
      else if (RDF.varp(b)) {
        if (e[b]) {
          return e[b] == a;
        } else {
          e[b] = a;
          return true;
        }
      }
      else {
        return a == b;
      }
    }
    else {
      return RDF.unify(RDF.subject(a), RDF.subject(b), e) 
      &&     RDF.unify(RDF.predicate(a), RDF.predicate(b), e) 
      &&     RDF.unify(RDF.object(a), RDF.object(b), e);
    }
  },

  select: function (variables,where) {
    var results = [];
    var g = new RDF.Goal();
    g.call = function (e) {
      var row = {};
      for (var v = 0; v <  variables.length; v++) {
        row[variables[v].substring(1)] = e[variables[v]];
      }
      results.push(row);
    }
    for (var p = where.length-1; p >= 0; p--) {
      var f = new RDF.Goal(where[p]);
      f.next = g;
      g = f;
    }
    g.call({});
    return results;
  },

  insert: function (s, p, o) {
    RDF.db.push({s: s, p: p, o: o});
  },

  anonId: 100,

  anonymousNode: function () {
    return '_'+RDF.anonId++;
  },

  parseXML: function (doc) {
    var root = doc.documentElement;
    var c = root.childNodes;
    for (var i = 0; i < c.length; i++) {
      RDF.parseXMLNode(c[i]);
    }
  },

  parseXMLNode: function (node) {
    // Text
    if (node.nodeType == 3) { 
      return node.nodeValue;
    }
    // Element
    else if (node.nodeType == 1) { 
      var about = node.getAttribute('rdf:about') || 
                  node.getAttribute('about') ||
                  RDF.anonymousNode();
      var type  = node.getAttribute('rdf:type') ||
                  node.getAttribute('type') ||
                  node.nodeName;
      RDF.insert(about, 'rdf:type', type);
      var c = node.childNodes;
      for (var i = 0; i < c.length; i++) {
        RDF.parseXMLProperty(c[i], about);
      }
      return about;
    }
  },

  parseXMLProperty: function (propNode, subject) {
    if (propNode.nodeType == 1) {
      var p = propNode.nodeName;
      var c = propNode.childNodes;
      for (var i = 0; i < c.length; i++) {
        var o = RDF.parseXMLNode(c[i]);
        if (o.match(/\S/)) { // Skip whitespace.
          RDF.insert(subject, p, o);
        }
      }
    }
  },

  db: []
       //{s: '#1',    p: 'rdf:type', o: 'Message'},
       //{s: 'INBOX', p: 'rdf:type', o: 'Mailbox'},
       //{s: '#2',    p: 'rdf:type', o: 'Message'},
       //{s: '#1',    p: 'envelope', o: 'e1'     },
       //{s: 'e1',    p: 'subject',  o: 'Hello'  },
       //{s: '#2',    p: 'envelope', o: 'e2'     },
       //{s: 'e2',    p: 'subject',  o: 'World!' },
       //{s: 'e1',    p: 'from',     o: 'Brent'  }
}

RDF.Goal = function(pattern) {
  this.pattern = pattern;
}

RDF.Goal.prototype.call = function(env) {
  var match = false;
  for (var i = 0; i < RDF.db.length; i++) {
    var e = Object.extend({}, env);
    if (RDF.unify(this.pattern, RDF.db[i], e)) {
      match = true;
      this.exit(e);
    }
  }
  if (match == false && this.pattern.optional) {
    this.exit(env);
  }
};

RDF.Goal.prototype.exit = function (e) {
  if (this.next) this.next.call(e);
};
