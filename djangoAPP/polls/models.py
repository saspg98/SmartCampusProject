from django.db import models
#MAX LENGHT OF FEEDBACK IS 200 CHARACTERS
from . import constants


class Feedback(models.Model):
    feedback_mssg = models.CharField(max_length=constants.FEEDBACK_MAX_LENGTH)
    pub_date = models.DateTimeField('date published')
    username = models.CharField(max_length=constants.USER_MAX_LENGTH)
    def __str__(self):
        return self.feedback_mssg

#TODO: Decide to remove/use this table
class FUpdate(models.Model):
    feedback = models.ForeignKey(Feedback, on_delete=models.CASCADE)
    updatecode = models.IntegerField(default=-1)
    def __str__(self):
        return feedback.feedback_mssg
