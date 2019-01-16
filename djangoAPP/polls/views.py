from django.shortcuts import render
from django.http import HttpResponse
from django.template import loader
from django.utils import timezone
from django.views.decorators.csrf import csrf_exempt
import csv

from .models import Feedback
from . import constants



# Create your views here.
def lastUpdated(request,access_key):
    #Display the latest value in the update model/table
    if access_key == constants.ACCESS_KEY:

        output = Feedback.objects.order_by('-pk')[:1]
        context = {'feedback':output,}
        return render(request, 'polls/last.html', context)

    else:#replace with 404, maybe
        output = "ACCESS DENIED!"
        return HttpResponse(output)



def newEntries(request, access_key, entry_num):
    #Display the required entries given the last update on the admin-client(primary key of the Feedback)
    response = HttpResponse(content_type='text/csv')
    response['Content-Disposition'] = 'attachment; filename="newData.csv"'
    #fieldnames = ['id', 'feedback_mssg', 'username','date']
    fieldnames = ['id', 'feedback_mssg', 'username']
    writer = csv.DictWriter(response, fieldnames=fieldnames)

    writer.writeheader()

    if entry_num <= 0:
        output = Feedback.objects.all().order_by('pk')
    else:
        output = Feedback.objects.filter(pk__gt = entry_num).order_by('pk')

    for fb in output:
        #writer.writerow({'id': fb.id, 'feedback_mssg': fb.feedback_mssg, 'username': fb.username, 'date':fb.pub_date})
        writer.writerow({'id': fb.id, 'feedback_mssg': fb.feedback_mssg, 'username': fb.username})

    return response


@csrf_exempt
def submitEntry(request):
    try:
        user = request.POST[constants.LABEL_USER]
        mssg = request.POST[constants.LABEL_MSSG]
    except (KeyError):
        return HttpResponse('Failure!No username or message!')
    else:
        f = Feedback(username=user,feedback_mssg=mssg, pub_date =timezone.now())
        f.save()

    #TODO: check for failure
    return HttpResponse('Successfully submitted review!')
