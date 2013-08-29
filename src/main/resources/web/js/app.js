
var ScalaMVD = (
  function(){

    var parseEvent = function(eventTemplate) {
      return function(raw) {
        //console.log(eventTemplate)
        var event = {
          name:               raw.name,
          description:        raw.description,
          rsvp_limit:         raw.rsvp_limit,
          maybe_rsvp_count:   raw.maybe_rsvp_count,
          waitlist_count:     raw.waitlist_count ,
          yes_rsvp_count:     raw.yes_rsvp_count,
          maybe_rsvp_count:   raw.maybe_rsvp_count,
          status:             raw.status,
          time:               new Date(raw.time),
          updated:            new Date(raw.updated), 
          event_url:          raw.event_url,
          venue: {
            name:          raw.venue.name,
            address:       raw.venue.address_1,
            lat:           raw.venue.lat,
            lon:           raw.venue.lon
          }
        }
        return _.template(eventTemplate,event)
      }

    }

    var fetchEvents = function() {
      var template = $("#eventTemplate").html();
      $.getJSON('meetup/events', function(data) {

        var meta    = data.meta;
        var results = data.results;

        var events = _.map(results, parseEvent(template));

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

