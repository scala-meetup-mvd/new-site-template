
var ScalaMVD = (
  function() {

    var parseEvent = function(eventTemplate, data) {
      return _.template(eventTemplate,data)
    }

    var fetchEvents = function() {
      var template = $("#eventTemplate").html();
      $.getJSON('meetup/events', function(data) {

        var meta    = data.meta;
        var results = data.results;

        var events = _.map(results, _.partial(parseEvent,template));

        if(events.length) $("#events").empty()

        _.each(events, function(event){
          $(event).appendTo("#events")
        })

      });

    }

    return {
      events: fetchEvents
    }

  }

)()

$.ready(ScalaMVD.events())

