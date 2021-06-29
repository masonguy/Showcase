import sqlite3

class dbInterface:

    def __init__(self):
        self.con = sqlite3.connect("upvote_dev.db")

    def getVote(self, postID):
        query = "SELECT * FROM votes WHERE postID = " + str(postID) + " LIMIT 1"
        rows = self.con.execute(query)
        row = rows.fetchone()
        if row == None:
            return (0,0,0)
        return row

    def createPost(self):
        query = "SELECT postID FROM votes ORDER BY postID DESC LIMIT 1"
        rows = self.con.execute(query)
        lastID = rows.fetchone()[0]
        newID = str(int(lastID) + 1)
        insertQuery = "INSERT INTO votes VALUES (" + str(newID) + ", 1, 0)"
        self.con.execute(insertQuery)
        return newID


    def vote(self, postID, voteType):
        post = self.getVote(postID)
        if post == (0,0,0):
            return 0
        if voteType == "upvote":
            newVote = post[1] + 1
            updateQuery = "UPDATE votes SET {voteType}s = {newVote} WHERE postID = {postID}".format(voteType = voteType, newVote = newVote, postID = postID)
            self.con.execute(updateQuery)
            return 1
        elif voteType == "downvote":
            newVote = post[2] + 1
            updateQuery = "UPDATE votes SET {voteType}s = {newVote} WHERE postID = {postID}".format(voteType = voteType, newVote = newVote, postID = postID)
            self.con.execute(updateQuery)
            return 1
        return 0

    def dump(self):
        query = "SELECT * FROM votes"
        rows = self.con.execute(query)
        retArray = []
        for row in rows:
            retArray.append(row)
        return retArray

    def shutdown(self):
        self.con.commit()
        self.con.close()
